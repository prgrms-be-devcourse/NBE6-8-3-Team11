package com.back.domain.member.controller

import com.back.domain.member.dto.request.LoginRequestDto
import com.back.domain.member.dto.response.TokenResponseDto
import com.back.domain.member.entity.Member
import com.back.domain.member.enums.UserRole
import com.back.domain.member.repository.MemberRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class MemberAdminControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private lateinit var userToken: String
    private lateinit var adminToken: String
    private var userId: Long = 0
    private var adminId: Long = 0

    @BeforeEach
    fun setUp() {
        memberRepository.deleteAll()

        val user = memberRepository.save(
            Member(
                email = "user@test.com",
                password = passwordEncoder.encode("password"),
                name = "일반유저",
                phone = "010-1111-1111",
                role = UserRole.USER
            )
        )

        val admin = memberRepository.save(
            Member(
                email = "admin@test.com",
                password = passwordEncoder.encode("password"),
                name = "관리자",
                phone = "010-2222-2222",
                role = UserRole.ADMIN
            )
        )

        userId = user.id
        adminId = admin.id

        userToken = getTokenViaLogin("user@test.com", "password")
        adminToken = getTokenViaLogin("admin@test.com", "password")
    }

    private fun getTokenViaLogin(email: String, password: String): String {
        val request = LoginRequestDto(email, password)
        val result = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val jsonResponse = result.response.contentAsString
        val contentJson = objectMapper.readTree(jsonResponse)["content"].toString()
        val tokenDto = objectMapper.readValue(contentJson, TokenResponseDto::class.java)
        return "Bearer ${tokenDto.accessToken}"
    }

    // ================= MemberController Test =================
    @Test
    @DisplayName("사용자 본인 정보 조회 성공")
    fun t1() {
        mockMvc.get("/api/members/{memberId}", userId) {
            header("Authorization", userToken)
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.email") { value("user@test.com") }
        }
    }

    // ================= AdminController Test =================
    @Test
    @DisplayName("관리자가 전체 회원 목록 조회 성공")
    fun t2() {
        mockMvc.get("/api/admin/members") {
            header("Authorization", adminToken)
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(2) }
        }
    }

    @Test
    @DisplayName("관리자가 특정 회원 정보 조회 성공")
    fun t3() {
        mockMvc.get("/api/admin/members/{memberId}", userId) {
            header("Authorization", adminToken)
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.email") { value("user@test.com") }
        }
    }

    @Test
    @DisplayName("관리자가 특정 회원 강제 탈퇴 성공")
    fun t4() {
        mockMvc.delete("/api/admin/members/{memberId}", userId) {
            header("Authorization", adminToken)
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("해당 회원을 삭제했습니다.") }
        }

        val isUserPresent = memberRepository.findById(userId).isPresent
        assertThat(isUserPresent).isFalse()
    }

    @Test
    @DisplayName("일반 유저가 관리자 API 호출시 Forbidden")
    fun t5() {
        mockMvc.get("/api/admin/members") {
            header("Authorization", userToken)
        }.andExpect {
            status { isForbidden() }
        }
    }
}