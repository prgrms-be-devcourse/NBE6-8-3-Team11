package com.back.domain.care.repository

import com.back.domain.care.entity.Care
import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface CareRepository : JpaRepository<Care, Long> {
    fun findByMemberOrderByCreatedAtDesc(member: Member): List<Care>

    fun findByIdAndMember(id: Long, member: Member): Care?

    fun findByIdAndPet_Member(id: Long, member: Member): Care?

    fun findByMember(member: Member): List<Care>

    fun findByPet_MemberOrderByCreatedAtDesc(member: Member): List<Care>
}