package com.back.domain.pet.controller

import com.back.domain.member.entity.Member
import com.back.domain.member.enums.UserRole
import com.back.domain.member.repository.MemberRepository
import com.back.domain.pet.dto.request.PetCreateRequestDto
import com.back.domain.pet.dto.request.PetUpdateRequestDto
import com.back.domain.pet.entity.Pet
import com.back.domain.pet.enums.Gender
import com.back.domain.pet.repository.PetRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertFalse

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PetControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val petRepository: PetRepository,
    val memberRepository: MemberRepository
) {

    lateinit var testMember: Member
    lateinit var otherMember: Member

    @BeforeEach
    fun setUp() {
        petRepository.deleteAll()
        memberRepository.deleteAll()

        testMember = Member(
            _email = "testuser@example.com",
            name = "테스트 유저",
            _password = "encoded-password",
            phone = "010-1234-5678",
            role = UserRole.USER
        )
        memberRepository.save(testMember)

        otherMember = Member(
            _email = "otheruser@example.com",
            name = "다른 유저",
            _password = "encoded-password",
            phone = "010-9876-5432",
            role = UserRole.USER
        )
        memberRepository.save(otherMember)
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 생성 성공 테스트")
    fun `t1 - create pet success`() {
        val dto = PetCreateRequestDto(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            shelterName = "",
            statuses = listOf("ADOPTED", "CARE_IN_PROGRESS")
        )

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.content.name").value("초코"))
            .andExpect(jsonPath("$.content.species").value("푸들"))
            .andExpect(jsonPath("$.content.age").value(3))
            .andExpect(jsonPath("$.content.gender").value("FEMALE"))
            .andExpect(jsonPath("$.content.description").value("활발한 강아지"))
            .andExpect(jsonPath("$.content.imageUrl").value("http://example.com/image.jpg"))
            .andExpect(jsonPath("$.content.shelterName").value("보호소 정보 없음"))
            .andExpect(jsonPath("$.content.petStatuses", hasSize<List<Any>>(2)))
            .andExpect(jsonPath("$.content.petStatuses", containsInAnyOrder("ADOPTED", "CARE_IN_PROGRESS")))
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 생성 실패 테스트 - 필수 필드 누락")
    fun `t2 - create pet fail missing name`() {
        val dto = PetCreateRequestDto(
            name = "",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            shelterName = "",
            statuses = listOf("ADOPTED")
        )

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("INPUT-400"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.content").isEmpty)
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 수정 성공 테스트")
    fun `t3 - update pet success`() {
        val pet = Pet.create(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            member = testMember
        )
        petRepository.save(pet)

        val updateDto = PetUpdateRequestDto(
            name = "초코-수정",
            species = "푸들",
            age = 4,
            gender = Gender.FEMALE,
            description = "더 활발해진 강아지",
            imageUrl = "http://example.com/newimage.jpg",
            shelterName = "",
            statuses = listOf("ADOPTED", "CARE_IN_PROGRESS")
        )

        mockMvc.perform(
            put("/api/pets/${pet.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.content.name").value("초코-수정"))
            .andExpect(jsonPath("$.content.age").value(4))
            .andExpect(jsonPath("$.content.description").value("더 활발해진 강아지"))
            .andExpect(jsonPath("$.content.imageUrl").value("http://example.com/newimage.jpg"))
            .andExpect(jsonPath("$.content.shelterName").value("보호소 정보 없음"))
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 수정 실패 - 존재하지 않는 펫 ID")
    fun `t4 - update pet fail not found`() {
        val updateDto = PetUpdateRequestDto(
            name = "초코-수정",
            species = "푸들",
            age = 4,
            gender = Gender.FEMALE,
            description = "더 활발해진 강아지",
            imageUrl = "http://example.com/newimage.jpg",
            shelterName = "",
            statuses = listOf("ADOPTED", "CARE_IN_PROGRESS")
        )

        mockMvc.perform(
            put("/api/pets/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("PET-404"))
            .andExpect(jsonPath("$.message").value("해당 동물을 찾을 수 없습니다."))
    }

    @Test
    @WithMockUser(username = "otheruser@example.com")
    @DisplayName("펫 수정 실패 - 권한 없음")
    fun `t5 - update pet fail forbidden`() {
        val pet = Pet.create(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            member = testMember
        )
        petRepository.save(pet)

        val updateDto = PetUpdateRequestDto(
            name = "수정된 이름",
            species = "푸들",
            age = 4,
            gender = Gender.FEMALE,
            description = "수정된 설명",
            imageUrl = "http://example.com/newimage.jpg",
            shelterName = "",
            statuses = emptyList()
        )

        mockMvc.perform(
            put("/api/pets/${pet.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("AUTH-403"))
            .andExpect(jsonPath("$.message").value("권한이 없습니다."))
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 삭제 성공 테스트")
    fun `t7 - delete pet success`() {
        val pet = Pet.create(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            member = testMember
        )
        petRepository.save(pet)

        mockMvc.perform(delete("/api/pets/${pet.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        val exists = petRepository.existsById(pet.id!!)
        assertFalse(exists)
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 삭제 실패 - 존재하지 않는 펫 ID")
    fun `t8 - delete pet fail not found`() {
        mockMvc.perform(delete("/api/pets/999999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("PET-404"))
            .andExpect(jsonPath("$.message").value("해당 동물을 찾을 수 없습니다."))
    }

    @Test
    @WithMockUser(username = "otheruser@example.com")
    @DisplayName("펫 삭제 실패 - 권한 없음")
    fun `t9 - delete pet fail forbidden`() {
        val pet = Pet.create(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            member = testMember
        )
        petRepository.save(pet)

        mockMvc.perform(delete("/api/pets/${pet.id}"))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("AUTH-403"))
            .andExpect(jsonPath("$.message").value("권한이 없습니다."))
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 단건 조회 성공 테스트")
    fun `t10 - get pet success`() {
        val pet = Pet.create(
            name = "초코",
            species = "푸들",
            age = 3,
            gender = Gender.FEMALE,
            description = "활발한 강아지",
            imageUrl = "http://example.com/image.jpg",
            member = testMember
        )
        petRepository.save(pet)

        mockMvc.perform(get("/api/pets/${pet.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.content.name").value("초코"))
            .andExpect(jsonPath("$.content.species").value("푸들"))
            .andExpect(jsonPath("$.content.age").value(3))
            .andExpect(jsonPath("$.content.gender").value("FEMALE"))
            .andExpect(jsonPath("$.content.description").value("활발한 강아지"))
            .andExpect(jsonPath("$.content.imageUrl").value("http://example.com/image.jpg"))
            .andExpect(jsonPath("$.content.shelterName").value("보호소 정보 없음"))
    }

    @Test
    @DisplayName("펫 단건 조회 실패 - 없는 ID")
    fun `t11 - get pet fail not found`() {
        mockMvc.perform(get("/api/pets/999999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("PET-404"))
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("펫 전체 조회 성공 테스트")
    fun `t12 - get all pets`() {
        val pet1 = Pet.create("초코", "푸들", 3, Gender.FEMALE, "활발한 강아지", "http://example.com/image1.jpg", member = testMember)
        val pet2 = Pet.create("콩이", "시추", 5, Gender.MALE, "귀여운 강아지", "http://example.com/image2.jpg", member = testMember)
        petRepository.saveAll(listOf(pet1, pet2))

        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.content", hasSize<Any>(2)))
            .andExpect(jsonPath("$.content[0].name").value("초코"))
            .andExpect(jsonPath("$.content[1].name").value("콩이"))
    }

    @Test
    @DisplayName("펫 전체 조회 - 여러 개")
    fun `t13 - get pets multiple`() {
        val pets = listOf(
            Pet.create("초코", "푸들", 3, Gender.FEMALE, "활발한 강아지", "http://example.com/image1.jpg", member = testMember),
            Pet.create("콩이", "시추", 5, Gender.MALE, "귀여운 강아지", "http://example.com/image2.jpg", member = testMember),
            Pet.create("몽이", "말티즈", 2, Gender.FEMALE, "얌전한 강아지", "http://example.com/image3.jpg", member = testMember)
        )
        petRepository.saveAll(pets)

        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.content", hasSize<Any>(3)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("초코", "콩이", "몽이")))
    }
}