package com.back.domain.pet.service

import com.back.domain.member.enums.UserRole
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class PetService(
    private val petRepository: PetRepository,
    private val memberRepository: MemberRepository,
    private val shelterRepository: ShelterRepository
) {

    // 반려동물 등록
    fun createPet(dto: PetCreateRequestDto, userEmail: String): PetInfoResponseDto {
        // 회원 조회
        val member = memberRepository.findByEmail(userEmail)
            .orElseThrow { PetException(PetErrorCode.MEMBER_NOT_FOUND) }

        // 보호소 조회 (nullable 처리)
        val shelter = dto.shelterName
            ?.takeIf { it.isNotBlank() }
            ?.let { shelterRepository.findByName(it).orElseThrow { PetException(PetErrorCode.SHELTER_NOT_FOUND) } }

        // Pet 엔티티 생성
        val pet = Pet.create(
            name = dto.name,
            species = dto.species,
            age = dto.age,
            gender = dto.gender,
            description = dto.description,
            imageUrl = dto.imageUrl,
            shelter = shelter,
            member = member
        )

        // 상태 변환
        val statuses = dto.statuses.map { status ->
            PetStatus.create(
                status = PetStatusType.valueOf(status),
                pet = pet
            )
        }
        pet.petStatuses = statuses.toMutableList()

        // 저장
        petRepository.save(pet)

        return PetInfoResponseDto.from(pet)
    }

    // 반려동물 삭제
    fun deletePet(petId: Long, userEmail: String) {
        val member = memberRepository.findByEmail(userEmail)
            .orElseThrow { PetException(PetErrorCode.MEMBER_NOT_FOUND) }

        val pet = petRepository.findById(petId)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }

        // 관리자 또는 소유자만 삭제 가능
        if (member.role != UserRole.ADMIN && pet.member?.id != member.id) {
            throw PetException(PetErrorCode.MEMBER_FORBIDDEN)
        }

        petRepository.delete(pet)
    }

    // 반려동물 수정
    fun updatePet(petId: Long, userEmail: String, requestDto: PetUpdateRequestDto): PetInfoResponseDto {
        val member = memberRepository.findByEmail(userEmail)
            .orElseThrow { PetException(PetErrorCode.MEMBER_NOT_FOUND) }

        val pet = petRepository.findById(petId)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }

        if (member.role != UserRole.ADMIN && pet.member?.id != member.id) {
            throw PetException(PetErrorCode.MEMBER_FORBIDDEN)
        }

        // Pet 엔티티에 DTO 내용 업데이트
        pet.updatePet(requestDto)

        return PetInfoResponseDto.from(pet)
    }

    //ID로 단일 반려동물 조회
    fun getPetById(id: Long): PetInfoResponseDto {
        val pet = petRepository.findById(id)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }
        return PetInfoResponseDto.from(pet)
    }

    //전체 반려동물 조회
    fun getAllPets(): List<PetInfoResponseDto> {
        return petRepository.findAll().map { PetInfoResponseDto.from(it) }
    }
}