package com.back.domain.adoption.dto.response

import com.back.domain.adoption.entity.Adoption
import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.care.entity.Care
import com.back.domain.pet.dto.response.PetInfoResponseDto
import java.time.LocalDateTime

data class ApplicationSimpleListResponseDto(
    val id: Long?,
    val title: String,
    val type: String,  // "ADOPTION" 또는 "CARE"
    val status: RequestStatus,
    val createdAt: LocalDateTime?,
    val petInfo: PetInfoResponseDto,  // Pet 정보 추가
    val desiredStartDate: LocalDateTime?,  // Care인 경우에만 사용
    val desiredEndDate: LocalDateTime? // Care인 경우에만 사용
) {
    companion object {
        fun fromAdoption(adoption: Adoption): ApplicationSimpleListResponseDto {
            return ApplicationSimpleListResponseDto(
                id = adoption.id,
                title = adoption.title,
                type = "ADOPTION",
                status = adoption.status,
                createdAt = adoption.createdAt,
                petInfo = PetInfoResponseDto.from(adoption.pet),
                desiredStartDate = null,
                desiredEndDate = null
            )
        }

        fun fromCare(care: Care): ApplicationSimpleListResponseDto {
            return ApplicationSimpleListResponseDto(
                id = care.id,
                title = care.title,
                type = "CARE",
                status = care.status,
                createdAt = care.createdAt,
                petInfo = PetInfoResponseDto.from(care.pet),
                desiredStartDate = care.desiredStartDate,
                desiredEndDate = care.desiredEndDate
            )
        }
    }
}