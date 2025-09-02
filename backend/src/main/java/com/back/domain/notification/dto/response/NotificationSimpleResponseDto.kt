package com.back.domain.notification.dto.response

import com.back.domain.notification.entity.Notification
import com.back.domain.notification.enums.NotificationType

data class NotificationSimpleResponseDto(
    val notificationId: Long?,
    val type: NotificationType,
    val title: String,
    val isRead: Boolean
) {
    companion object {
        fun from(notification: Notification): NotificationSimpleResponseDto {
            return NotificationSimpleResponseDto(
                notificationId = notification.id,
                type = notification.type,
                title = notification.title,
                isRead = notification.isRead
            )
        }
    }
}
