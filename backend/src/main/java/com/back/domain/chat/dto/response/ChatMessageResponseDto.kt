package com.back.domain.chat.dto.response

import com.back.domain.chat.entity.ChatMessage
import java.time.LocalDateTime

data class ChatMessageResponseDto(
    val messageId: Long?,
    val roomId: Long?,
    val senderId: Long?,
    val senderName: String?,
    val content: String?,
    val sentAt: LocalDateTime?
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageResponseDto {
            return ChatMessageResponseDto(
                messageId = chatMessage.id,
                roomId = chatMessage.chatRoom.id,
                senderId = chatMessage.sender.id,
                senderName = chatMessage.sender.name,
                content = chatMessage.content,
                sentAt = chatMessage.sentAt
            )
        }
    }
}