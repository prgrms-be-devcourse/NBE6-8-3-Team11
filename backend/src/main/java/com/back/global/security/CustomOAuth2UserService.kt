package com.back.global.security

import com.back.domain.member.entity.Member
import com.back.domain.member.enums.UserRole
import com.back.domain.member.repository.MemberRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        // 카카오에서 받아온 사용자 정보
        val attributes = oAuth2User.attributes
        val kakaoAccount = attributes["kakao_account"] as? Map<String, Any>
        val email = kakaoAccount?.get("email") as? String

        //이메일 정보가 없으면 예외를 발생시켜 로그인을 중단
        if (email == null) {
            throw OAuth2AuthenticationException("카카오 계정에서 이메일을 가져올 수 없습니다.")
        }

        //DB에 해당 이메일 사용자 있는지 확인
        val member = memberRepository.findByEmail(email).orElseGet {
            val profile = kakaoAccount["profile"] as? Map<String, Any>
            val nickname = profile?.get("nickname") as? String

            val newMember = Member(
                email = email,
                password = "OAUTH2_USER_PASSWORD",

                name = nickname ?: "사용자",
                phone = "N/A",
                role = UserRole.USER
            )
            memberRepository.save(newMember)
        }

        return CustomOAuth2User(oAuth2User, member)
    }
}