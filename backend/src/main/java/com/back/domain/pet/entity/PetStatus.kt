package com.back.domain.pet.entity

import com.back.domain.pet.enums.PetStatusType
import jakarta.persistence.*

@Entity
@Table(name = "pet_status")
class PetStatus protected constructor(
    @Enumerated(EnumType.STRING)
    @Column(name = "pet_status_type", nullable = false)
    var status: PetStatusType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    var pet: Pet
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_status_id")
    val id: Long? = null


    fun updateStatus(newStatus: PetStatusType) {
        this.status = newStatus
    }

    companion object {
        fun create(status: PetStatusType, pet: Pet): PetStatus {
            return PetStatus(status, pet)
        }
    }

}
