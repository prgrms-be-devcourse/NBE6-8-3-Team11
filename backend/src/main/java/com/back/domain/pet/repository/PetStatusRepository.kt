package com.back.domain.pet.repository

import com.back.domain.pet.entity.Pet
import com.back.domain.pet.entity.PetStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PetStatusRepository : JpaRepository<PetStatus, Long> {
    fun findByPet(pet: Pet): List<PetStatus>
}