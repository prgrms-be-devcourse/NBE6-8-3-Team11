package com.back.domain.care.entity

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
@Table(name = "care")
@EntityListeners(AuditingEntityListener::class)
class Care private constructor(
    @field:Column(name = "care_title", nullable = false)
    var title: String,
    
    @field:Column(name = "care_message", nullable = false)
    @field:Lob
    var message: String,
    
    @field:Column(name = "care_desired_start_date")
    var desiredStartDate: LocalDateTime,
    
    @field:Column(name = "care_another_pets")
    var anotherPets: String,
    
    @field:Column(name = "care_experience")
    @field:Lob
    var experience: String,
    
    @field:Column(name = "care_desired_end_date")
    var desiredEndDate: LocalDateTime,
    
    @field:Column(name = "care_status", nullable = false)
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
    @Column(name = "care_id")
    var id: Long? = null
        private set

    @CreatedDate
    val createdAt: LocalDateTime? = null

    @OneToMany(mappedBy = "care", cascade = [CascadeType.ALL], orphanRemoval = true)
    val notifications: MutableList<Notification> = mutableListOf()

    fun updateStatus(newStatus: RequestStatus) {
        status = newStatus
    }

    companion object {
        fun create(
            title: String,
            message: String,
            desiredStartDate: LocalDateTime,
            anotherPets: String,
            experience: String,
            desiredEndDate: LocalDateTime,
            member: Member,
            pet: Pet,
            applicant: Applicant,
            status: RequestStatus = RequestStatus.PENDING
        ): Care {
            return Care(
                title = title,
                message = message,
                desiredStartDate = desiredStartDate,
                anotherPets = anotherPets,
                experience = experience,
                desiredEndDate = desiredEndDate,
                status = status,
                member = member,
                pet = pet,
                applicant = applicant
            )
        }
    }
}