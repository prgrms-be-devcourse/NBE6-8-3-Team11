package com.back.domain.shelter.service

import com.back.domain.shelter.dto.response.ShelterResponseDto
import com.back.domain.shelter.entity.Shelter
import com.back.domain.shelter.entity.toDto // *** Shelter.kt에 만든 확장 함수를 import 합니다.
import com.back.domain.shelter.repository.ShelterRepository
import com.back.global.exception.CustomException
import com.back.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShelterService(
    private val shelterRepository: ShelterRepository
) {

    @Transactional(readOnly = true)
    fun getShelterById(shelterId: Long): ShelterResponseDto {
        val shelter = findShelterById(shelterId)
        return shelter.toDto()
    }

    fun findShelterById(shelterId: Long): Shelter {
        return shelterRepository.findById(shelterId)
            .orElseThrow { CustomException(ErrorCode.SHELTER_NOT_FOUND) }
    }
}