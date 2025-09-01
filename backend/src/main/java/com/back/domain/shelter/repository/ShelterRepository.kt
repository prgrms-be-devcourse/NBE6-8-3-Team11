package com.back.domain.shelter.repository

import com.back.domain.shelter.entity.Shelter
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ShelterRepository : JpaRepository<Shelter, Long> {
    fun findByName(name: String): Optional<Shelter>
}