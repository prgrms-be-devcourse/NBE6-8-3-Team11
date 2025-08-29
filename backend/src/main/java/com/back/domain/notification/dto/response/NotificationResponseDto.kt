package com.back.domain.notification.dto.response

import com.back.domain.notification.entity.Notification
import com.back.domain.notification.enums.NotificationType
import java.time.LocalDateTime

data class NotificationResponseDto(
    val notificationId: Long?,
    val type: NotificationType,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(notification: Notification): NotificationResponseDto {
            return NotificationResponseDto(
                notificationId = notification.id,
                type = notification.type,
                title = notification.title,
                message = notification.message,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        }
    }
}
