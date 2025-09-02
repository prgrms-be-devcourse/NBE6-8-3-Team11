package com.back.global.security

import com.back.domain.member.dto.response.TokenResponseDto
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.Duration
import java.util.*

@Component
class JwtProvider(
    @Value("\${jwt.secret}") secretKey: String,
    @Value("\${jwt.access-token-expiration}") accessTokenExpiration: String,
    @Value("\${jwt.refresh-token-expiration}") refreshTokenExpiration: String
) {
    private val key: Key
    private val accessTokenExpiration: Long
    private val refreshTokenExpiration: Long

    init {
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        this.key = Keys.hmacShaKeyFor(keyBytes)
        this.accessTokenExpiration = Duration.parse(accessTokenExpiration).toMillis()
        this.refreshTokenExpiration = Duration.parse(refreshTokenExpiration).toMillis()
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    fun generateToken(authentication: Authentication): TokenResponseDto {
        val authorities = authentication.authorities.joinToString(",") { it.authority }

        var email: String? = null
        var nickname: String? = null
        var id: Long? = null

        when (val principal = authentication.principal) {
            is CustomAuthentication -> {
                val member = principal.member
                email = member.email
                nickname = member.name
                id = member.id
            }
            is CustomOAuth2User -> {
                email = principal.email
                nickname = principal.nickname
                id = principal.id
            }
            is UserDetails -> {
                email = principal.username
            }
        }

        val now = Date().time

        // Access Token 생성
        val accessTokenExpiresIn = Date(now + this.accessTokenExpiration)
        val accessToken = Jwts.builder()
            .setSubject(email)
            .claim("auth", authorities)
            .claim("email", email)
            .claim("nickname", nickname)
            .claim("id", id)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        // Refresh Token 생성
        val refreshTokenExpiresIn = Date(now + this.refreshTokenExpiration)
        val refreshToken = Jwts.builder()
            .setSubject(email)
            .setExpiration(refreshTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenResponseDto(
            grantType = "Bearer",
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = id ?: 0L, // *** id가 null일 경우 기본값을 사용하도록 안전하게 처리
            userEmail = email ?: "",
            userName = nickname ?: ""
        )
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)

        val authorities: Collection<GrantedAuthority> =
            claims["auth"]?.toString()?.split(",")
                ?.filter { it.isNotBlank() }
                ?.map { SimpleGrantedAuthority(it) }
                ?: emptyList() // *** 권한 정보가 없으면 빈 리스트를 반환

        val email = claims["email"] as String
        val principal: UserDetails = User(email, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    // 토큰 정보를 검증하는 메서드
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            when (e) {
                is SecurityException, is MalformedJwtException -> log.info("잘못된 JWT 서명입니다.")
                is ExpiredJwtException -> log.info("만료된 JWT 토큰입니다.")
                is UnsupportedJwtException -> log.info("지원되지 않는 JWT 토큰입니다.")
                is IllegalArgumentException -> log.info("JWT 토큰이 잘못되었습니다.")
            }
            false
        }
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtProvider::class.java)
    }
}