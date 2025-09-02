package com.back.domain.care.dto.response

import com.back.domain.applicant.dto.response.ApplicantResponseDto
import com.back.domain.care.entity.Care
import java.time.LocalDateTime

data class CareResponseDto(
    val careId: Long?,
    val petId: Long?,
    val memberId: Long?,
    val applicantInfo: ApplicantResponseDto,
    val title: String,
    val message: String,
    val anotherPets: String?,
    val experience: String?,
    val desiredStartDate: LocalDateTime,
    val desiredEndDate: LocalDateTime?,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(care: Care): CareResponseDto {
            return CareResponseDto(
                careId = care.id,
                petId = care.pet.id,
                memberId = care.member.id,
                applicantInfo = ApplicantResponseDto(
                    id = care.applicant.id,
                    name = care.applicant.name,
                    phone = care.applicant.phone,
                    email = care.applicant.email,
                    address = care.applicant.address
                ),
                title = care.title,
                message = care.message,
                anotherPets = care.anotherPets,
                experience = care.experience,
                desiredStartDate = care.desiredStartDate,
                desiredEndDate = care.desiredEndDate,
                createdAt = care.createdAt
            )
        }
    }
}
