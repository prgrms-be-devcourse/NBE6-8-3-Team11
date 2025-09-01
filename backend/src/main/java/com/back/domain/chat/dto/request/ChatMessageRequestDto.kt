package com.back.domain.chat.dto.request

data class ChatMessageRequestDto(
    val roomId: Long,
    val senderId: Long,
    val content: String
) 