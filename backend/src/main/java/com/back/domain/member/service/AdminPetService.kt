//package com.back.domain.member.service
//
//import com.back.domain.member.repository.MemberRepository
//import com.back.domain.pet.dto.request.PetCreateRequestDto
//import com.back.domain.pet.dto.request.PetUpdateRequestDto
//import com.back.domain.pet.dto.response.PetInfoResponseDto
//import com.back.domain.pet.entity.Pet
//import com.back.domain.pet.entity.PetStatus
//import com.back.domain.pet.enums.PetStatusType
//import com.back.domain.pet.exception.PetErrorCode
//import com.back.domain.pet.exception.PetException
//import com.back.domain.pet.repository.PetRepository
//import com.back.domain.shelter.repository.ShelterRepository
//import com.back.global.exception.CustomException
//import com.back.global.exception.ErrorCode
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//
//@Service
//@Transactional
//class AdminPetService(
//    private val petRepository: PetRepository,
//    private val shelterRepository: ShelterRepository,
//    private val memberRepository: MemberRepository
//) {
//
//    // 새로운 동물 등록
//    fun createPet(requestDto: PetCreateRequestDto, adminEmail: String): PetInfoResponseDto {
//        val admin = memberRepository.findByEmail(adminEmail)
//            .orElseThrow { PetException(PetErrorCode.MEMBER_NOT_FOUND) }
//
//        // 코틀린의 if-expression을 사용하여 shelter를 간결하게 조회
//        val shelter = if (!requestDto.shelterName.isNullOrBlank()) {
//            shelterRepository.findByName(requestDto.shelterName)
//                .orElseThrow { CustomException(ErrorCode.SHELTER_NOT_FOUND) }
//        } else {
//            null
//        }
//
//        // Lombok의 @Builder 대신 코틀린의 주 생성자와 이름 붙은 인자(named arguments) 사용
//        val pet = Pet(
//            name = requestDto.name,
//            species = requestDto.species,
//            age = requestDto.age,
//            gender = requestDto.gender,
//            description = requestDto.description,
//            imageUrl = requestDto.imageUrl,
//            shelter = shelter,
//            member = admin // 관리자 멤버로 추가
//        )
//
//        // Java Stream 대신 코틀린의 컬렉션 함수(map) 사용
//        val statuses = requestDto.statuses?.map { status ->
//            PetStatus(
//                status = PetStatusType.valueOf(status),
//                pet = pet
//            )
//        } ?: emptyList() // requestDto.statuses가 null이면 빈 리스트를 사용
//
//        pet.petStatuses.addAll(statuses)
//
//        val savedPet = petRepository.save(pet)
//        return savedPet.toDto() // DTO 변환을 위한 확장 함수 사용 (가정)
//    }
//
//    // 모든 동물 조회
//    @Transactional(readOnly = true)
//    fun getAllPets(): List<PetInfoResponseDto> {
//        return petRepository.findAll().map { it.toDto() }
//    }
//
//    // 특정 동물 조회
//    @Transactional(readOnly = true)
//    fun getPetById(petId: Long): PetInfoResponseDto {
//        val pet = petRepository.findById(petId)
//            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }
//        return pet.toDto()
//    }
//
//    // 동물 정보 수정
//    fun updatePet(petId: Long, requestDto: PetUpdateRequestDto): PetInfoResponseDto {
//        val pet = petRepository.findById(petId)
//            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }
//
//        val shelter = if (!requestDto.shelterName.isNullOrBlank()) {
//            shelterRepository.findByName(requestDto.shelterName)
//                .orElseThrow { CustomException(ErrorCode.SHELTER_NOT_FOUND) }
//        } else {
//            null
//        }
//
//        pet.updatePet(requestDto, shelter) // updatePet 메소드가 shelter를 받도록 수정했다고 가정
//
//        pet.petStatuses.clear()
//
//        val newStatuses = requestDto.statuses?.map { status ->
//            PetStatus(
//                status = PetStatusType.valueOf(status),
//                pet = pet
//            )
//        } ?: emptyList()
//
//        pet.petStatuses.addAll(newStatuses)
//
//        return pet.toDto()
//    }
//
//    // 동물 정보 삭제
//    fun deletePet(petId: Long) {
//        if (!petRepository.existsById(petId)) {
//            throw PetException(PetErrorCode.PET_NOT_FOUND)
//        }
//        petRepository.deleteById(petId)
//    }
//}