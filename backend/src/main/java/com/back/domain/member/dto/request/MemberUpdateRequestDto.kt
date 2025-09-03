package com.back.domain.member.dto.request

data class MemberUpdateRequestDto(
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val memberType: String? = null
)