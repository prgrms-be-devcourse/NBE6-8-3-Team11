package com.back.domain.shelter.entity

import com.back.domain.shelter.dto.response.ShelterResponseDto

fun Shelter.toDto(): ShelterResponseDto {
    return ShelterResponseDto(
        id = this.id,
        name = this.name,
        address = this.address,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode,
        phone = this.phone,
        createdAt = this.createdAt
    )
}