package com.back.domain.adoption.dto.request

import jakarta.validation.constraints.NotNull

data class AdoptionCareStatusUpdateRequestDto(
    val id: @NotNull Long = 0L,
    val type: @NotNull String = "",  // "ADOPTION" 또는 "CARE"
    val status: @NotNull String = "" // "ACCEPTED", "REJECTED"
)
