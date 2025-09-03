package com.back.domain.chat.dto.request

data class ChatMessageRequestDto(
    val roomId: Long = 0L,
    val senderId: Long = 0L,
    val content: String = ""
)