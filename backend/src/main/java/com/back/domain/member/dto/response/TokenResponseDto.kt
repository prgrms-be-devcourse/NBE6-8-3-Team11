package com.back.domain.member.dto.response


data class TokenResponseDto(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val userEmail: String,
    val userName: String
) 