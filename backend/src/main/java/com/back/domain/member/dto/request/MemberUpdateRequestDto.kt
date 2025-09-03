package com.back.domain.member.dto.request


data class MemberUpdateRequestDto(
    val name: String?,
    val phone: String?,
    //String currentPassword,
    //String newPassword,
    val address: String?,
    val bio: String?,
    val memberType: String?

) 