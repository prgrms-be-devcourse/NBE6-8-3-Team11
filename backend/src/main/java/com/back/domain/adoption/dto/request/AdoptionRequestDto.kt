package com.back.domain.adoption.dto.request

import com.back.domain.applicant.dto.request.ApplicantRequestDto
import jakarta.validation.constraints.NotNull

data class AdoptionRequestDto(
    val petId: @NotNull Long = 0L,
    val title: @NotNull String = "",
    val applicantInfo: ApplicantRequestDto = ApplicantRequestDto(),
    val anotherPets: String? = null,
    val experience: String? = null,
    val message: String? = null
)
