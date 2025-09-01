package com.back.domain.pet.controller

import com.back.domain.member.entity.Member
import com.back.domain.member.enums.UserRole
import com.back.domain.member.repository.MemberRepository
import com.back.domain.pet.dto.request.PetCreateRequestDto
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

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

    lateinit var testMember:  Member
    lateinit var otherMember: Member

    @BeforeEach
    fun setUp() {
        petRepository.deleteAll()
        memberRepository.deleteAll()

        testMember = Member(
            email = "testuser@example.com",
            name = "테스트 유저",
            _password = "encoded-password",
            phone = "010-1234-5678",
            role = UserRole.USER
        )
        memberRepository.save(testMember)

        otherMember = Member(
            email = "otheruser@example.com",
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

    // 나머지 테스트도 동일하게 변환 가능
}
