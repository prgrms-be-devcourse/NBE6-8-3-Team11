package com.back.domain.pet.entity

import com.back.domain.adoption.entity.Adoption
import com.back.domain.care.entity.Care
import com.back.domain.member.entity.Member
import com.back.domain.pet.dto.request.PetUpdateRequestDto
import com.back.domain.pet.enums.Gender
import com.back.domain.shelter.entity.Shelter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "pet")
@EntityListeners(AuditingEntityListener::class)
class Pet protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    val id: Long? = null,

    @Column(name = "pet_name", nullable = false)
    var name: String,

    @Column(name = "pet_species", nullable = false)
    var species: String,

    @Column(name = "pet_age")
    var age: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_gender", nullable = false)
    var gender: Gender,

    @Lob
    @Column(name = "pet_description")
    var description: String? = null,

    @Column(name = "pet_image_url")
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", nullable = true)
    var shelter: Shelter? = null,

) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    lateinit var member: Member

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val adoptions: MutableList<Adoption> = mutableListOf()

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.ALL], orphanRemoval = true)
    var petStatuses: MutableList<PetStatus> = mutableListOf()

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val cares: MutableList<Care> = mutableListOf()


    fun updatePet(dto: PetUpdateRequestDto) {
        dto.name?.let { this.name = it }
        dto.species?.let { this.species = it }
        dto.age?.let { this.age = it }
        dto.gender?.let { this.gender = it }
        dto.description?.let { this.description = it }
        dto.imageUrl?.let { this.imageUrl = it }
    }
    companion object {
        fun create(
            name: String,
            species: String,
            age: Int,
            gender: Gender,
            description: String? = null,
            imageUrl: String? = null,
            shelter: Shelter? = null,
            member: Member,
            statuses: List<PetStatus> = emptyList()
        ): Pet {
            val pet = Pet(
                name = name,
                species = species,
                age = age,
                gender = gender,
                description = description,
                imageUrl = imageUrl,
                shelter = shelter,
            )
            pet.member = member
            pet.petStatuses = statuses.toMutableList()
            return pet
        }
    }
}