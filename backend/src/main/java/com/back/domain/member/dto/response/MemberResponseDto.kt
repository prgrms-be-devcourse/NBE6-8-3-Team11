package com.back.domain.member.dto.response

import java.time.LocalDateTime

//회원가입 성공하고 줄 데이터
data class MemberResponseDto(
    val memberId: Long,
    val email: String,
    val name: String,
    val phone: String?,
    val address: String?,
    val bio: String?,
    val createdAt: LocalDateTime
)