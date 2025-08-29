package com.back.global.security

import com.back.domain.member.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val oAuth2User: OAuth2User,
    val member: Member
) : OAuth2User by oAuth2User {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return member.authorities
    }

    val email: String
        get() = member.email

    val nickname: String
        get() = member.name

    val id: Long
        get() = member.id
}