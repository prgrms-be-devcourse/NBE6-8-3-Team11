package com.back.domain.pet.dto.response

import com.back.domain.pet.entity.Pet
import java.time.LocalDateTime

data class PetInfoResponseDto(
    val id: Long,
    val petOwnerId: Long, // 항상 존재
    val name: String,
    val species: String,
    val age: Int,
    val gender: String,
    val description: String?,
    val imageUrl: String?,
    val shelterName: String, // 보호소 정보 없으면 기본값 처리
    val createdAt: LocalDateTime?,
    val petStatuses: List<String>
) {
    companion object {
        fun from(pet: Pet): PetInfoResponseDto {
            return PetInfoResponseDto(
                id = pet.id!!,
                petOwnerId = pet.member!!.id, // 항상 존재하므로 !!
                name = pet.name,
                species = pet.species,
                age = pet.age,
                gender = pet.gender.name,
                description = pet.description,
                imageUrl = pet.imageUrl,
                shelterName = pet.shelter?.name ?: "보호소 정보 없음",
                createdAt = pet.createdAt,
                petStatuses = pet.petStatuses.map { it.status.name }
            )
        }
    }
}