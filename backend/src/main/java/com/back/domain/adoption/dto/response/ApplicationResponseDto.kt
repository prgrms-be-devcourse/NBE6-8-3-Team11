package com.back.domain.adoption.dto.response

import com.back.domain.adoption.entity.Adoption
import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.applicant.dto.response.ApplicantResponseDto
import com.back.domain.care.entity.Care
import com.back.domain.pet.dto.response.PetInfoResponseDto
import java.time.LocalDateTime

data class ApplicationResponseDto(
    val id: Long?,
    val title: String,
    val type: String,  // "ADOPTION" 또는 "CARE"
    val applicantInfo: ApplicantResponseDto,
    val anotherPets: String?,
    val experience: String?,
    val message: String?,
    val status: RequestStatus,
    val createdAt: LocalDateTime?,
    val petInfo: PetInfoResponseDto,
    val desiredStartDate: LocalDateTime?,  // Care인 경우에만 사용
    val desiredEndDate: LocalDateTime? // Care인 경우에만 사용
) {
    companion object {
        fun fromAdoption(adoption: Adoption): ApplicationResponseDto? {
            return ApplicationResponseDto(
                id = adoption.id,
                title = adoption.title,
                type = "ADOPTION",
                applicantInfo = ApplicantResponseDto(
                    id = adoption.applicant.id,
                    name = adoption.applicant.name,
                    phone = adoption.applicant.phone,
                    email = adoption.applicant.email,
                    address = adoption.applicant.address
                ),
                anotherPets = adoption.anotherPets,
                experience = adoption.experience,
                message = adoption.message,
                status = adoption.status,
                createdAt = adoption.createdAt,
                petInfo = PetInfoResponseDto.from(adoption.pet),
                desiredStartDate = null,
                desiredEndDate = null
            )
        }

        fun fromCare(care: Care): ApplicationResponseDto? {
            return ApplicationResponseDto(
                id = care.id,
                title = care.title,
                type = "CARE",
                applicantInfo = ApplicantResponseDto(
                    id = care.applicant.id,
                    name = care.applicant.name,
                    phone = care.applicant.phone,
                    email = care.applicant.email,
                    address = care.applicant.address
                ),
                anotherPets = care.anotherPets,
                experience = care.experience,
                message = care.message,
                status = care.status,
                createdAt = care.createdAt,
                petInfo = PetInfoResponseDto.from(care.pet),
                desiredStartDate = care.desiredStartDate,
                desiredEndDate = care.desiredEndDate
            )
        }
    }
}