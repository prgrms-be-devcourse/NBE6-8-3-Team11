package com.back.domain.shelter.entity

import com.back.domain.pet.entity.Pet
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "shelter")
@EntityListeners(AuditingEntityListener::class)
class Shelter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelter_id")
    val id: Long = 0L,

    @Column(name = "shelter_name", nullable = false)
    var name: String,

    @Column(name = "shelter_address", nullable = false)
    var address: String,

    @Column(name = "shelter_city")
    var city: String?,

    @Column(name = "shelter_state")
    var state: String?,

    @Column(name = "shelter_zip_code")
    var zipCode: String?,

    @Column(name = "shelter_phone")
    var phone: String?,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(mappedBy = "shelter", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pets: MutableList<Pet> = mutableListOf()
}