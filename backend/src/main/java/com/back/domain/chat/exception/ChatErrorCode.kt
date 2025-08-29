package com.back.domain.chat.exception

import org.springframework.http.HttpStatus

enum class ChatErrorCode(
    val httpStatus: HttpStatus?,
    val code: String?,
    val message: String?
) {
    MEMBER_NOT_CHAT_ROOM_MEMBER(HttpStatus.NOT_FOUND, "CHAT-404", "채탕방 내에서 회원을 찾을 수 없습니다.");
}