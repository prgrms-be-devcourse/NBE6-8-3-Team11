package com.back.domain.member.dto.request

import jakarta.validation.constraints.NotBlank

data class ReissueRequestDto(
        @field:NotBlank(message = "Refresh Token은 필수 입력 값입니다.")
        val refreshToken: String
)

