package com.back.domain.adoption.controller

import com.back.domain.adoption.dto.request.AdoptionCareStatusUpdateRequestDto
import com.back.domain.adoption.dto.request.AdoptionRequestDto
import com.back.domain.adoption.entity.Adoption
import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.adoption.repository.AdoptionRepository
import com.back.domain.applicant.dto.request.ApplicantRequestDto
import com.back.domain.applicant.entity.Applicant
import com.back.domain.care.entity.Care
import com.back.domain.care.repository.CareRepository
import com.back.domain.member.entity.Member
import com.back.domain.member.enums.UserRole
import com.back.domain.member.repository.MemberRepository
import com.back.domain.pet.entity.Pet
import com.back.domain.pet.entity.PetStatus
import com.back.domain.pet.enums.Gender
import com.back.domain.pet.enums.PetStatusType
import com.back.domain.pet.repository.PetRepository
import com.back.domain.pet.repository.PetStatusRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
internal class AdoptionControllerIntegrationTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var petStatusRepository: PetStatusRepository

    @Autowired
    private lateinit var adoptionRepository: AdoptionRepository

    @Autowired
    private lateinit var careRepository: CareRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc
    private lateinit var testMember: Member
    private lateinit var petOwner: Member
    private lateinit var testPet: Pet
    private lateinit var testAdoption: Adoption
    private lateinit var testCare: Care
    private lateinit var testApplicant: Applicant

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()

        // 테스트 회원 생성 (신청자)
        testMember = Member(
            email = "test@example.com",
            name = "테스트회원",
            password = "password123",
            phone = "010-1234-5678",
            role = UserRole.USER
        )
        memberRepository.save(testMember)

        // 펫 소유자 생성
        petOwner = Member(
            email = "owner@example.com",
            name = "펫소유자",
            password = "password123",
            phone = "010-8765-4321",
            role = UserRole.USER
        )
        memberRepository.save(petOwner)

        // 테스트 펫 생성
        testPet = Pet.create(
            name = "테스트펫",
            species = "강아지",
            age = 3,
            gender = Gender.MALE,
            description = "친근한 강아지입니다.",
            member = petOwner
        )
        petRepository.save(testPet)

        // 펫 상태 설정 (입양 가능)
        val petStatus = PetStatus.create(
            status = PetStatusType.AVAILABLE_FOR_ADOPTION,
            pet = testPet
        )
        petStatusRepository.save(petStatus)

        // Pet 엔티티의 petStatuses 리스트에 추가
        testPet.petStatuses.add(petStatus)

        // Applicant 엔티티 생성
        testApplicant = Applicant.create(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        // 테스트 입양 신청 생성 (petOwner가 소유한 펫에 대한 신청)
        testAdoption = Adoption.create(
            title = "입양 신청합니다",
            anotherPets = null,
            experience = null,
            message = "정성스럽게 키우겠습니다.",
            member = testMember, // testMember가 신청자
            pet = testPet, // testPet은 petOwner가 소유
            applicant = testApplicant
        )
        adoptionRepository.save(testAdoption)

        // 테스트 돌봄 신청 생성
        testCare = Care.create(
            title = "돌봄 신청합니다",
            message = "정성스럽게 돌봐드리겠습니다.",
            desiredStartDate = LocalDateTime.now().plusDays(1),
            anotherPets = "없음",
            experience = "강아지 키워본 경험 있음",
            desiredEndDate = LocalDateTime.now().plusDays(7),
            member = testMember,
            pet = testPet,
            applicant = testApplicant
        )
        careRepository.save(testCare)
    }

    @Test
    @DisplayName("입양 신청 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyAdoption_Success() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = AdoptionRequestDto(
            petId = testPet.id!!,
            title = "입양 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "강아지 1마리",
            experience = "돌봄 경험이 있습니다.",
            message = "정성스럽게 키우겠습니다."
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/adoption")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.title").value("입양 신청합니다"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.message").value("정성스럽게 키우겠습니다."))
    }

    @Test
    @DisplayName("입양 신청 - 존재하지 않는 펫")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyAdoption_PetNotFound() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = AdoptionRequestDto(
            petId = 999L,
            title = "입양 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "강아지 1마리",
            experience = "돌봄 경험이 있습니다.",
            message = "정성스럽게 키우겠습니다."
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/adoption")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @DisplayName("입양 신청 - 입양 불가능한 펫")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyAdoption_PetNotAvailableForAdoption() {
        // given - 펫 상태를 입양 불가능으로 변경
        val petStatuses = petStatusRepository.findByPet(testPet)
        petStatuses.stream()
            .filter { petStatus -> petStatus.status == PetStatusType.AVAILABLE_FOR_ADOPTION }
            .forEach { petStatus ->
                petStatus.updateStatus(PetStatusType.ADOPTED)
                petStatusRepository.save(petStatus)
            }

        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = AdoptionRequestDto(
            petId = testPet.id!!,
            title = "입양 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "강아지 1마리",
            experience = "돌봄 경험이 있습니다.",
            message = "정성스럽게 키우겠습니다."
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/adoption")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @DisplayName("회원 입양/돌봄 신청 목록 조회 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun getAdoptionAndCareList_Success() {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/applies"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2)) // 입양 1개 + 돌봄 1개
    }

    @Test
    @DisplayName("회원 입양/돌봄 신청 내역 상세 조회 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun getAdoptionAndCareDetail_Success() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/applies/detail")
                .param("typeId", testAdoption.id.toString())
                .param("type", "ADOPTION")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.title").value("입양 신청합니다"))
    }

    @Test
    @DisplayName("회원 입양/돌봄 신청 내역 단건 삭제 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun deleteAdoptionAndCare_Success() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/applies")
                .param("typeId", testAdoption.id.toString())
                .param("type", "ADOPTION")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("회원 입양/돌봄 신청 내역 전체 삭제 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun deleteAdoptionAndCareAll_Success() {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/applies/all"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 신청 내역 리스트 조회 - 성공")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun getReceivedApplications_Success() {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/applies/received"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 신청 내역 상세 조회 - 성공")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun getReceivedApplicationDetail_Success() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/applies/received/detail")
                .param("typeId", testAdoption.id.toString())
                .param("type", "ADOPTION")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.title").value("입양 신청합니다"))
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 신청 내역 상태 변경 - 수락")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun updateReceivedApplicationStatus_Accept() {
        // given
        val requestDto = AdoptionCareStatusUpdateRequestDto(
            id = testAdoption.id!!,
            type = "ADOPTION",
            status = "ACCEPTED"
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/applies/received")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 신청 내역 상태 변경 - 거절")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun updateReceivedApplicationStatus_Reject() {
        // given
        val requestDto = AdoptionCareStatusUpdateRequestDto(
            id = testAdoption.id!!,
            type = "ADOPTION",
            status = "REJECTED"
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/applies/received")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 등록 내역 단건 삭제 - 성공")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun deleteReceivedApplication_Success() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/applies/received")
                .param("typeId", testAdoption.id.toString())
                .param("type", "ADOPTION")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("보호자가 받은 입양/돌봄 등록 내역 전체 삭제 - 성공")
    @WithMockUser(username = "owner@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun deleteReceivedApplicationsAll_Success() {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/applies/received/all"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
    }

    @Test
    @DisplayName("입양 신청 - 권한 없음")
    @Throws(Exception::class)
    fun applyAdoption_Unauthorized() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = AdoptionRequestDto(
            petId = testPet.id!!,
            title = "입양 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "강아지 1마리",
            experience = "돌봄 경험이 있습니다.",
            message = "정성스럽게 키우겠습니다."
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/adoption")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isFound()) // 302 - 인증되지 않은 사용자는 OAuth2 로그인 페이지로 리다이렉트
    }
}