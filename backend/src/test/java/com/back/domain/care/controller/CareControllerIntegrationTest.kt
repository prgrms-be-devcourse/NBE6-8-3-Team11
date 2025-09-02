package com.back.domain.care.controller

import CareRequestDto
import com.back.domain.applicant.dto.request.ApplicantRequestDto
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
import org.springframework.http.RequestEntity.post
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
internal class CareControllerIntegrationTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var petStatusRepository: PetStatusRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc
    private lateinit var testMember: Member
    private lateinit var testPet: Pet

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()

        // 테스트 회원 생성
        testMember = Member(
            email = "test@example.com",
            name = "테스트회원",
            password = "password123",
            phone = "010-1234-5678",
            role = UserRole.USER
        )
        memberRepository.save(testMember)

        // 테스트 펫 생성
        testPet = Pet.create(
            name = "테스트펫",
            species = "강아지",
            age = 3,
            gender = Gender.MALE,
            description = "친근한 강아지입니다.",
            member = testMember
        )
        petRepository.save(testPet)

        // 펫 상태 설정 (돌봄 가능)
        val petStatus = PetStatus.create(
            status = PetStatusType.AVAILABLE_FOR_CARE,
            pet = testPet
        )
        petStatusRepository.save(petStatus)

        // Pet 엔티티의 petStatuses 리스트에 추가
        testPet.petStatuses.add(petStatus)
    }

    @Test
    @DisplayName("돌봄 신청 - 성공")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyCare_Success() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = CareRequestDto(
            petId = testPet.id!!,
            title = "돌봄 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "없음",
            experience = "강아지 키워본 경험 있음",
            message = "정성스럽게 돌봐드리겠습니다.",
            desiredStartDate = LocalDateTime.now().plusDays(1),
            desiredEndDate = LocalDateTime.now().plusDays(7)
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/care")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isOk())
    }

    @Test
    @DisplayName("돌봄 신청 - 존재하지 않는 펫")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyCare_PetNotFound() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = CareRequestDto(
            petId = 999L,
            title = "돌봄 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "없음",
            experience = "강아지 키워본 경험 있음",
            message = "정성스럽게 돌봐드리겠습니다.",
            desiredStartDate = LocalDateTime.now().plusDays(1),
            desiredEndDate = LocalDateTime.now().plusDays(7)
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/care")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @DisplayName("돌봄 신청 - 돌봄 불가능한 펫")
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    @Throws(Exception::class)
    fun applyCare_PetNotAvailableForCare() {
        // given - 펫 상태를 돌봄 불가능으로 변경
        val petStatuses = petStatusRepository.findByPet(testPet)
        petStatuses.stream()
            .filter { petStatus -> petStatus.status == PetStatusType.AVAILABLE_FOR_CARE }
            .forEach { petStatus ->
                petStatus.updateStatus(PetStatusType.CARE_COMPLETED)
                petStatusRepository.save(petStatus)
            }

        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = CareRequestDto(
            petId = testPet.id!!,
            title = "돌봄 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "없음",
            experience = "강아지 키워본 경험 있음",
            message = "정성스럽게 돌봐드리겠습니다.",
            desiredStartDate = LocalDateTime.now().plusDays(1),
            desiredEndDate = LocalDateTime.now().plusDays(7)
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/care")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @DisplayName("돌봄 신청 - 권한 없음")
    @Throws(Exception::class)
    fun applyCare_Unauthorized() {
        // given
        val applicantInfo = ApplicantRequestDto(
            name = "테스트신청자",
            phone = "010-1234-5678",
            email = "test@example.com",
            address = "서울시 강남구"
        )

        val requestDto = CareRequestDto(
            petId = testPet.id!!,
            title = "돌봄 신청합니다",
            applicantInfo = applicantInfo,
            anotherPets = "없음",
            experience = "강아지 키워본 경험 있음",
            message = "정성스럽게 돌봐드리겠습니다.",
            desiredStartDate = LocalDateTime.now().plusDays(1),
            desiredEndDate = LocalDateTime.now().plusDays(7)
        )

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/applies/care")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(MockMvcResultMatchers.status().isFound()) // 302 - Spring Security에서 인증되지 않은 요청은 302로 리다이렉트
    }
}