package com.back.domain.chat.dto.response

import com.back.domain.chat.entity.ChatRoom
import java.time.LocalDateTime

data class ChatRoomResponseDto(
    val id: Long?,
    val createdAt: LocalDateTime?,
    val firstMemberId: Long?,
    val secondMemberId: Long?
) {
    companion object {
        fun from(chatRoom: ChatRoom): ChatRoomResponseDto {
            return ChatRoomResponseDto(
                id = chatRoom.id,
                createdAt = chatRoom.createdAt,
                firstMemberId = chatRoom.firstMember.id,
                secondMemberId = chatRoom.secondMember.id
            )
        }
    }
}