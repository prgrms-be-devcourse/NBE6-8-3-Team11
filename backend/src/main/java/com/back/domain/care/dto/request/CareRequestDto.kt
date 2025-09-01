package com.back.domain.care.dto.request

import com.back.domain.applicant.dto.request.ApplicantRequestDto
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CareRequestDto(
    val petId: @NotNull Long,
    val title: @NotNull String,
    val applicantInfo: ApplicantRequestDto,
    val anotherPets: String,
    val experience: String,
    val message: String,
    val desiredStartDate: @NotNull LocalDateTime,
    /**
     * 요청자가 원하는 돌봄 종료 날짜 - 무기한이 가능하므로 비어있을 수 있음
     */
    val desiredEndDate: LocalDateTime
)
