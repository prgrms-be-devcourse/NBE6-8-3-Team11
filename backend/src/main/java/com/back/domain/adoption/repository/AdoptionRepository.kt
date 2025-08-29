package com.back.domain.adoption.repository

import com.back.domain.adoption.entity.Adoption
import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface AdoptionRepository : JpaRepository<Adoption, Long> {
    fun findByMemberOrderByCreatedAtDesc(member: Member): List<Adoption>

    fun findByIdAndMember(id: Long, member: Member): Adoption?

    // 언더스코어 주의 문제 다시 체킹
    fun findByIdAndPet_Member(id: Long, member: Member): Adoption?

    fun findByMember(member: Member): List<Adoption>

    // 언더스코어 주의 문제 다시 체킹
    fun findByPet_MemberOrderByCreatedAtDesc(member: Member): List<Adoption>
}