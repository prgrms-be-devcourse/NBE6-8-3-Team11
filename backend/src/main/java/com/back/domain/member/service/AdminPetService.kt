package com.back.domain.member.service

import com.back.domain.member.repository.MemberRepository
import com.back.domain.pet.dto.request.PetCreateRequestDto
import com.back.domain.pet.dto.request.PetUpdateRequestDto
import com.back.domain.pet.dto.response.PetInfoResponseDto
import com.back.domain.pet.entity.Pet
import com.back.domain.pet.entity.PetStatus
import com.back.domain.pet.enums.PetStatusType
import com.back.domain.pet.exception.PetErrorCode
import com.back.domain.pet.exception.PetException
import com.back.domain.pet.repository.PetRepository
import com.back.domain.shelter.repository.ShelterRepository
import com.back.global.exception.CustomException
import com.back.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminPetService(
    private val petRepository: PetRepository,
    private val shelterRepository: ShelterRepository,
    private val memberRepository: MemberRepository
) {
    fun createPet(requestDto: PetCreateRequestDto, adminEmail: String): PetInfoResponseDto {
        val admin = memberRepository.findByEmail(adminEmail)
            .orElseThrow { PetException(PetErrorCode.MEMBER_NOT_FOUND) }

        val shelter = if (!requestDto.shelterName.isNullOrBlank()) {
            shelterRepository.findByName(requestDto.shelterName)
                .orElseThrow { CustomException(ErrorCode.SHELTER_NOT_FOUND) }
        } else {
            null
        }

        val pet = Pet.create(
            name = requestDto.name,
            species = requestDto.species,
            age = requestDto.age,
            gender = requestDto.gender,
            description = requestDto.description,
            imageUrl = requestDto.imageUrl,
            shelter = shelter,
            member = admin
        )

        val statuses = requestDto.statuses.map { status ->
            PetStatus.create(
                status = PetStatusType.valueOf(status),
                pet = pet
            )
        }

        pet.petStatuses.addAll(statuses)
        val savedPet = petRepository.save(pet)
        return PetInfoResponseDto.from(savedPet)
    }

    // 모든 동물 조회
    @Transactional(readOnly = true)
    fun getAllPets(): List<PetInfoResponseDto> {
        return petRepository.findAll().map { PetInfoResponseDto.from(it) }
    }

    // 특정 동물 조회
    @Transactional(readOnly = true)
    fun getPetById(petId: Long): PetInfoResponseDto {
        val pet = petRepository.findById(petId)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }
        return PetInfoResponseDto.from(pet)
    }

    // 동물 정보 수정
    fun updatePet(petId: Long, requestDto: PetUpdateRequestDto): PetInfoResponseDto {
        val pet = petRepository.findById(petId)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }

        val shelterName = requestDto.shelterName

        val shelter = if (!shelterName.isNullOrBlank()) {
            shelterRepository.findByName(shelterName)
                .orElseThrow { CustomException(ErrorCode.SHELTER_NOT_FOUND) }
        } else {
            null
        }
        pet.shelter = shelter

        pet.updatePet(requestDto)

        pet.petStatuses.clear()
        val newStatuses = requestDto.statuses?.map { status ->
            PetStatus.create(
                status = PetStatusType.valueOf(status),
                pet = pet
            )
        } ?: emptyList()
        pet.petStatuses.addAll(newStatuses)

        return PetInfoResponseDto.from(pet)
    }

    // 동물 정보 삭제
    fun deletePet(petId: Long) {
        if (!petRepository.existsById(petId)) {
            throw PetException(PetErrorCode.PET_NOT_FOUND)
        }
        petRepository.deleteById(petId)
    }
}