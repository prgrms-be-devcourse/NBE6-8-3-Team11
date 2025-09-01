package com.back.global.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtProvider: JwtProvider,
    @Value("\${FRONTEND_URL:http://localhost:3000}") private val frontendUrl: String
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        //JWT 토큰 생성
        val tokenResponseDto = jwtProvider.generateToken(authentication)
        val accessToken = tokenResponseDto.accessToken
        val refreshToken = tokenResponseDto.refreshToken

        // 프론트엔드로 리디렉션할 URL 생성
        val targetUrl = UriComponentsBuilder.fromUriString("$frontendUrl/oauth2/redirect")
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .build().toUriString()

        // *** 리디렉션
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}