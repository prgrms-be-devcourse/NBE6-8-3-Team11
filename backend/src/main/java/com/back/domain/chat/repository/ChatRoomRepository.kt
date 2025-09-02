package com.back.domain.chat.repository

import com.back.domain.chat.entity.ChatRoom
import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.firstMember = :member1 AND cr.secondMember = :member2) OR (cr.firstMember = :member2 AND cr.secondMember = :member1)")
    fun findByMembers(@Param("member1") member1: Member, @Param("member2") member2: Member): ChatRoom?

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.firstMember = :member OR cr.secondMember = :member")
    fun findByMember(@Param("member") member: Member): List<ChatRoom>
}