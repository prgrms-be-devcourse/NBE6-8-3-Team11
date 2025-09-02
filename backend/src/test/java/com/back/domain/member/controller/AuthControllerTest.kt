package com.back.domain.member.controller

import com.back.domain.member.dto.request.LoginRequestDto
import com.back.domain.member.dto.request.ReissueRequestDto
import com.back.domain.member.dto.request.SignUpRequestDto
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
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class AuthControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private lateinit var testUser: Member

    @BeforeEach
    fun setUp() {
        memberRepository.deleteAll()
        // 모든 테스트에서 사용할 기본 사용자를 미리 생성
        testUser = memberRepository.save(
            Member(
                email = "test@test.com",
                _password = passwordEncoder.encode("password123"),
                name = "테스터",
                phone = "010-0000-0000",
                role = UserRole.USER
            )
        )
    }

    @Test
    @DisplayName("POST /api/auth/join - 회원가입 성공")
    fun signUp_Success() {
        // given
        val requestDto = SignUpRequestDto(
            email = "newuser@test.com",
            password = "password",
            name = "새사용자",
            phone = "010-1234-5678"
        )

        // when & then
        mockMvc.post("/api/auth/join") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestDto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.content.email") { value("newuser@test.com") }
        }
    }

    @Test
    @DisplayName("POST /api/auth/login - 로그인 성공 및 토큰 발급")
    fun login_Success() {
        // given
        val requestDto = LoginRequestDto("test@test.com", "password123")

        // when & then
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestDto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.content.accessToken") { exists() }
            jsonPath("$.content.refreshToken") { exists() }
        }
    }

    @Test
    @DisplayName("POST /api/auth/reissue - 토큰 재발급 성공")
    fun reissueToken_Success() {
        // given
        // 1. 로그인하여 Refresh Token을 DB에 저장하고, 응답에서 받아온다.
        val loginDto = LoginRequestDto("test@test.com", "password123")
        val loginResult = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginDto)
        }.andReturn()

        val responseString = loginResult.response.contentAsString
        val responseJson = objectMapper.readTree(responseString)
        val refreshToken = responseJson["content"]["refreshToken"].asText()

        // 2. 재발급 요청 DTO를 생성한다.
        val reissueDto = ReissueRequestDto(refreshToken)

        // when & then
        mockMvc.post("/api/auth/reissue") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(reissueDto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.message") { value("토큰이 재발급 됐습니다.") }
            jsonPath("$.content.accessToken") { exists() }
        }
    }

    @Test
    @DisplayName("DELETE /api/auth/{memberId} - 회원 탈퇴 성공")
    fun deleteMember_Success() {
        // given
        // 1. 로그인하여 Access Token을 얻는다.
        val loginDto = LoginRequestDto("test@test.com", "password123")
        val loginResult = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginDto)
        }.andReturn()

        val responseString = loginResult.response.contentAsString
        val responseJson = objectMapper.readTree(responseString)
        val accessToken = responseJson["content"]["accessToken"].asText()

        // when & then
        mockMvc.delete("/api/auth/{memberId}", testUser.id) {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.message") { value("회원 탈퇴가 완료되었습니다.") }
        }

        // DB에서 실제로 삭제되었는지 확인
        val isPresent = memberRepository.findById(testUser.id).isPresent
        assertThat(isPresent).isFalse()
    }
}