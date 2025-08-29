package com.back.domain.chat.exception

import org.springframework.http.HttpStatus

class ChatException(chatErrorCode: ChatErrorCode) : RuntimeException(chatErrorCode.message) {
    val httpStatus: HttpStatus = chatErrorCode.httpStatus
    val code: String = chatErrorCode.code
    val message: String = chatErrorCode.message
}