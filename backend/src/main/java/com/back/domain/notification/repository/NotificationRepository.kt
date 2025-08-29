package com.back.domain.notification.repository

import com.back.domain.member.entity.Member
import com.back.domain.notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByMemberOrderByCreatedAtDesc(member: Member): List<Notification>

    fun findByIdAndMember(id: Long, member: Member): Notification?

    fun findByMember(member: Member): List<Notification>
}
