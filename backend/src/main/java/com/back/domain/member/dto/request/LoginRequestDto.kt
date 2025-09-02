package com.back.domain.member.dto.request

data class LoginRequestDto(
    val email: String = "",
    val password: String = ""
)
