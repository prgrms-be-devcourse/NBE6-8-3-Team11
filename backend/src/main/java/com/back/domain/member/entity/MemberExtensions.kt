package com.back.domain.member.entity

import com.back.domain.member.dto.response.MemberResponseDto


fun Member.toDto():MemberResponseDto{
    return MemberResponseDto(
        memberId = this.id,
        email = this.email,
        name = this.name,
        phone = this.phone,
        address = this.address,
        bio = this.bio,
        createdAt = this.createdAt
    )
}