package com.back.global.security

import com.back.domain.member.repository.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserDetailsServiceImpl(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        log.debug("사용자 인증 시도: {}", email)
        val member = memberRepository.findByEmail(email)
            .orElseThrow {
                log.warn("사용자를 찾을 수 없음: {}", email)
                UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.: $email")
            }
        return member
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)
    }
}