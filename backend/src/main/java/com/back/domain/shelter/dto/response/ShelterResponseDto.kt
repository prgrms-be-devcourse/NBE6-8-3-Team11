package com.back.domain.shelter.dto.response

import java.time.LocalDateTime

data class ShelterResponseDto(
    val id: Long,
    val name: String,
    val address: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val phone: String?,
    val createdAt: LocalDateTime
)