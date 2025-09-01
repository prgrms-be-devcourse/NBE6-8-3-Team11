package com.back.domain.pet.dto.request

import com.back.domain.pet.enums.Gender
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PetUpdateRequestDto(
    @field:NotBlank(message = "이름은 필수입니다.")
    var name: String,

    @field:NotBlank(message = "품종은 필수입니다.")
    var species: String,

    @field:Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    var age: Int = 0,

    @field:NotNull(message = "성별은 필수입니다.")
    var gender: Gender,

    var description: String? = null,
    var imageUrl: String? = null,
    var shelterName: String? = null,
    var statuses: List<String> = emptyList()
)