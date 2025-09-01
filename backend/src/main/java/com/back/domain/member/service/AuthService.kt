package com.back.domain.member.service

import com.back.domain.member.dto.request.LoginRequestDto
import com.back.domain.member.dto.request.SignUpRequestDto
import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.dto.response.TokenResponseDto
import com.back.domain.member.entity.Member
import com.back.domain.member.entity.toDto // *** Member.kt에 만든 확장 함수를 import 한다.
import com.back.domain.member.enums.UserRole
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import com.back.global.security.CustomAuthentication
import com.back.global.security.JwtProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtProvider: JwtProvider
) {

    fun signUp(requestDto: SignUpRequestDto): MemberResponseDto {
        if (memberRepository.findByEmail(requestDto.email).isPresent) {
            throw MemberException(MemberErrorCode.EMAIL_ALREADY_EXISTS)
        }

        val encodedPassword = passwordEncoder.encode(requestDto.password)

        val newMember = Member(
            _email = requestDto.email,
            _password = encodedPassword,
            name = requestDto.name,
            phone = requestDto.phone,
            role = UserRole.USER
        )

        return memberRepository.save(newMember).toDto()
    }

    fun login(requestDto: LoginRequestDto): TokenResponseDto {
        val member = memberRepository.findByEmail(requestDto.email)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        // 인증 토큰 생성
        val authenticationToken =
            UsernamePasswordAuthenticationToken(requestDto.email, requestDto.password)
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)

        // 사용자 정보가 포함된 커스텀 Authentication 생성
        val customAuth = CustomAuthentication(authentication, member)

        // 토큰 생성 (Member 정보 포함)
        val tokenResponse = jwtProvider.generateToken(customAuth)

        //refresh토큰 db에 저장
        member.updateRefreshToken(tokenResponse.refreshToken)

        // 사용자 정보 포함하여 반환
        return TokenResponseDto(
            grantType = tokenResponse.grantType,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            userId = member.id,
            userEmail = member.email,
            userName = member.name
        )
    }

    @Transactional
    fun reissueToken(refreshToken: String): TokenResponseDto {
        // 1. Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw MemberException(MemberErrorCode.INVALID_TOKEN)
        }

        // 2. DB에서 Refresh Token으로 사용자를 찾음
        val member = memberRepository.findByRefreshToken(refreshToken)
            .orElseThrow { MemberException(MemberErrorCode.TOKEN_NOT_FOUND) }

        // 3. 새로운 토큰 생성
        val customAuth = CustomAuthentication.from(member)
        val newTokenResponse = jwtProvider.generateToken(customAuth)

        // 4. DB에 새로운 Refresh Token으로 업데이트 (Refresh Token Rotation)
        member.updateRefreshToken(newTokenResponse.refreshToken)

        // 5. 사용자 정보 포함하여 새로운 토큰 정보 반환
        return TokenResponseDto(
            grantType = newTokenResponse.grantType,
            accessToken = newTokenResponse.accessToken,
            refreshToken = newTokenResponse.refreshToken,
            userId = member.id,
            userEmail = member.email,
            userName = member.name
        )
    }

    fun deleteMember(memberId: Long, userDetails: UserDetails) {
        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }
        memberRepository.delete(member)
    }
}