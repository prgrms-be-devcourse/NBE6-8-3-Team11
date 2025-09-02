package com.back.domain.notification.entity

import com.back.domain.adoption.entity.Adoption
import com.back.domain.care.entity.Care
import com.back.domain.member.entity.Member
import com.back.domain.notification.enums.NotificationType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener::class)
class Notification private constructor(
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "notification_type", nullable = false)
    var type: NotificationType,

    @field:Column(name = "notification_title", nullable = false)
    var title: String,

    @field:Column(name = "notification_message", nullable = false)
    @field:Lob
    var message: String,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "adoption_id")
    var adoption: Adoption?,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "care_id")
    var care: Care?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    var id: Long? = null
        private set

    @Column(name = "notification_is_read")
    var isRead: Boolean = false

    @CreatedDate
    val createdAt: LocalDateTime? = null

    fun markAsRead() {
        isRead = !isRead
    }

    companion object {
        fun create(
            type: NotificationType,
            title: String,
            message: String,
            member: Member,
            adoption: Adoption? = null,
            care: Care? = null
        ): Notification {
            return Notification(
                type = type,
                title = title,
                message = message,
                member = member,
                adoption = adoption,
                care = care
            )
        }
    }
}