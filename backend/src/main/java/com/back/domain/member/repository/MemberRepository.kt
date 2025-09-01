package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m._email = :email")
    fun findByEmail(email: String?): Optional<Member>
    
    fun findByRefreshToken(refreshToken: String): Optional<Member>
}
