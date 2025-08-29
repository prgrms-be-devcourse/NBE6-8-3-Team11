package com.back.domain.adoption.dto.request

import com.back.domain.applicant.dto.request.ApplicantRequestDto
import jakarta.validation.constraints.NotNull

data class AdoptionRequestDto(
    val petId: @NotNull Long,
    val title: @NotNull String,
    val applicantInfo: ApplicantRequestDto,
    val anotherPets: String?,
    val experience: String?,
    val message: String?
)
