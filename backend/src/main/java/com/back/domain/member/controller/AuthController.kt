package com.back.domain.member.controller

import com.back.domain.member.dto.request.LoginRequestDto
import com.back.domain.member.dto.request.ReissueRequestDto
import com.back.domain.member.dto.request.SignUpRequestDto
import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.dto.response.TokenResponseDto
import com.back.domain.member.service.AuthService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다")
    @PostMapping("/join")
    fun signUp(@Valid @RequestBody requestDto: SignUpRequestDto): ResponseEntity<ApiResponse<MemberResponseDto>> {
        val responseDto = authService.signUp(requestDto)
        return ResponseEntity.ok(ApiResponse.success(responseDto))
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    fun login(@Valid @RequestBody requestDto: LoginRequestDto): ResponseEntity<ApiResponse<TokenResponseDto>> {
        return ResponseEntity.ok(ApiResponse.success(authService.login(requestDto)))
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "회원 탈퇴")
    fun deleteMember(
        @PathVariable memberId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void?>> {
        authService.deleteMember(memberId, userDetails)
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null))
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급") // *** Swagger Operation을 추가하여 API 명세를 명확히 했습니다.
    fun reissue(@Valid @RequestBody requestDto: ReissueRequestDto): ResponseEntity<ApiResponse<TokenResponseDto>> {
        val tokenResponseDto = authService.reissueToken(requestDto.refreshToken)
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급 됐습니다.", tokenResponseDto))
    }
}