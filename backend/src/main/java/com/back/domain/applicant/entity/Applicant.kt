package com.back.domain.applicant.entity

import com.back.domain.adoption.entity.Adoption
import com.back.domain.care.entity.Care
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "applicant")
@EntityListeners(AuditingEntityListener::class)
class Applicant private constructor(
    @field:Column(name = "applicant_name", nullable = false)
    var name: String,
    
    @field:Column(name = "applicant_phone", nullable = false)
    var phone: String,
    
    @field:Column(name = "applicant_email", nullable = false)
    var email: String,
    
    @field:Column(name = "applicant_address", nullable = false)
    var address: String,
    
    @field:OneToOne(mappedBy = "applicant")
    var adoption: Adoption?,
    
    @field:OneToOne(mappedBy = "applicant")
    var care: Care?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    var id: Long? = null
        private set

    companion object {
        fun create(
            name: String,
            phone: String,
            email: String,
            address: String,
            adoption: Adoption? = null,
            care: Care? = null
        ): Applicant {
            return Applicant(
                name = name,
                phone = phone,
                email = email,
                address = address,
                adoption = adoption,
                care = care
            )
        }
    }
}
