package com.back.global.security

import com.back.domain.member.entity.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class CustomAuthentication(
    private val authentication: Authentication,
    val member: Member
) : Authentication by authentication {
    //클래스 위임으로 간단하게

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + member.role.name))
    }

    fun getMember(): Member {
        return member
    }

    companion object {
        fun from(member: Member): CustomAuthentication {
            val authentication: Authentication = UsernamePasswordAuthenticationToken(
                member.email,
                null,  //비번
                listOf(SimpleGrantedAuthority("ROLE_" + member.role.name))
            )
            return CustomAuthentication(authentication, member)
        }
    }
}