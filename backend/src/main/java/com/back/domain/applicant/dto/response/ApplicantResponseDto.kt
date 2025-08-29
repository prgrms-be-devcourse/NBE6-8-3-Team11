package com.back.domain.applicant.dto.response

import com.back.domain.applicant.entity.Applicant

data class ApplicantResponseDto(
    val id: Long?,
    val name: String,
    val phone: String,
    val email: String,
    val address: String
) {
    companion object {
        fun from(applicant: Applicant): ApplicantResponseDto? {
            return ApplicantResponseDto(
                id = applicant.id,
                name = applicant.name,
                phone = applicant.phone,
                email = applicant.email,
                address = applicant.address
            )
        }
    }
}
