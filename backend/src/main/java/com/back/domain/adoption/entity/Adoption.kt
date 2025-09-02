package com.back.domain.adoption.entity

import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.applicant.entity.Applicant
import com.back.domain.member.entity.Member
import com.back.domain.notification.entity.Notification
import com.back.domain.pet.entity.Pet
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "adoption")
@EntityListeners(AuditingEntityListener::class)
class Adoption private constructor(
    @field:Column(name = "adoption_title", nullable = false)
    var title: String,
    
    @field:Column(name = "adoption_another_pets")
    var anotherPets: String?,
    
    @field:Lob
    @field:Column(name = "adoption_experience")
    var experience: String?,
    
    @field:Column(name = "adoption_message", nullable = false)
    @field:Lob
    var message: String?,
    
    @field:Column(name = "adoption_status", nullable = false)
    @field:Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,
    
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "member_id", nullable = false)
    var member: Member,
    
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "pet_id", nullable = false)
    var pet: Pet,
    
    @field:OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @field:JoinColumn(name = "applicant_id")
    var applicant: Applicant
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adoption_id")
    var id: Long? = null
        private set

    @CreatedDate
    lateinit var createdAt: LocalDateTime
        protected set

    @OneToMany(mappedBy = "adoption", cascade = [CascadeType.ALL], orphanRemoval = true)
    val notifications: MutableList<Notification> = mutableListOf()

    fun updateStatus(newStatus: RequestStatus) {
        status = newStatus
    }

    companion object {
        fun create(
            title: String,
            anotherPets: String?,
            experience: String?,
            message: String?,
            member: Member,
            pet: Pet,
            applicant: Applicant,
            status: RequestStatus = RequestStatus.PENDING
        ): Adoption {
            return Adoption(
                title = title,
                anotherPets = anotherPets,
                experience = experience,
                message = message,
                status = status,
                member = member,
                pet = pet,
                applicant = applicant
            )
        }
    }
}
