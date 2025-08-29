package com.back.domain.chat.exception

import org.springframework.http.HttpStatus

class ChatException(val chatErrorCode: ChatErrorCode) : RuntimeException(chatErrorCode.message) {
    val httpStatus: HttpStatus get() = chatErrorCode.httpStatus
    val code: String get() = chatErrorCode.code
}