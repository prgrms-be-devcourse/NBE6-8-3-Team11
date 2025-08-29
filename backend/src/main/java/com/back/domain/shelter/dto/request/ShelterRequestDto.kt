package com.back.domain.shelter.dto.request

import jakarta.validation.constraints.NotBlank

data class ShelterRequestDto(
    @field:NotBlank(message = "보호소 이름은 필수입니다.")
    val name: String,

    @field:NotBlank(message = "보호소 주소는 필수입니다.")
    val address: String,

    val city: String?,
    val state: String?,
    val zipCode: String?,
    val phone: String?
)