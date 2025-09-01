package com.back.domain.adoption.dto.response

import com.back.domain.adoption.entity.Adoption
import com.back.domain.applicant.dto.response.ApplicantResponseDto
import java.time.LocalDateTime

data class AdoptionResponseDto(
    val adoptionId: Long?,
    val petId: Long?,
    val memberId: Long?,
    val applicantInfo: ApplicantResponseDto,
    val anotherPets: String?,
    val experience: String?,
    val title: String,
    val message: String?,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(adoption: Adoption): AdoptionResponseDto? {
            return AdoptionResponseDto(
                adoptionId = adoption.id,
                petId = adoption.pet.id,
                memberId = adoption.member.id,
                applicantInfo = ApplicantResponseDto(
                    id = adoption.applicant.id,
                    name = adoption.applicant.name,
                    phone = adoption.applicant.phone,
                    email = adoption.applicant.email,
                    address = adoption.applicant.address
                ),
                title = adoption.title,
                anotherPets = adoption.anotherPets,
                experience = adoption.experience,
                message = adoption.message,
                createdAt = adoption.createdAt
            )
        }
    }
}
