package com.back.domain.chat.dto

import com.back.domain.chat.entity.ChatMessage
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ChatMessageDto(
    val id: Long?,
    val content: String?,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val sentAt: LocalDateTime?,
    val chatRoomId: Long?,
    val senderId: Long?,
    val senderName: String?
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageDto {
            return ChatMessageDto(
                id = chatMessage.id,
                content = chatMessage.content,
                sentAt = chatMessage.sentAt,
                chatRoomId = chatMessage.chatRoom.id,
                senderId = chatMessage.sender.id,
                senderName = chatMessage.sender.name
            )
        }
    }
}
