package com.back.domain.applicant.dto.request

import com.back.domain.applicant.entity.Applicant
import jakarta.validation.constraints.NotNull

data class ApplicantRequestDto(
    val name: @NotNull String = "",
    val phone: @NotNull String = "",
    val email: @NotNull String = "",
    val address: @NotNull String = ""
) {
    companion object {
        fun of(applicantRequestDto: ApplicantRequestDto): Applicant {
            return Applicant.create(
                name = applicantRequestDto.name,
                phone = applicantRequestDto.phone,
                email = applicantRequestDto.email,
                address = applicantRequestDto.address
            )
        }
    }
}
