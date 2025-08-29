package com.back.domain.adoption.repository

import com.back.domain.adoption.entity.Adoption
import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface AdoptionRepository : JpaRepository<Adoption, Long> {
    fun findByMemberOrderByCreatedAtDesc(member: Member): List<Adoption>

    fun findByIdAndMember(id: Long, member: Member): Adoption?

    fun findByIdAndPet_Member(id: Long, member: Member): Adoption?

    fun findByMember(member: Member): List<Adoption>

    fun findByPet_MemberOrderByCreatedAtDesc(member: Member): List<Adoption>
}