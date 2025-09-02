package com.back.domain.member.entity

import com.back.domain.adoption.entity.Adoption
import com.back.domain.care.entity.Care
import com.back.domain.member.enums.UserRole
import com.back.domain.notification.entity.Notification
import com.back.domain.pet.entity.Pet
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long = 0L,

    @Column(name = "member_email", nullable = false, unique = true)
    var email: String,


    @Column(name = "member_password", nullable = false)
    private var password: String,

    @Column(name = "member_name", nullable = false)
    var name: String,

    @Column(name = "member_phone")
    var phone: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    var role: UserRole,

    @Column(name = "refresh_token")
    var refreshToken: String? = null,

    @Column(name = "member_address")
    var address: String? = null,

    @Lob
    @Column(name = "member_bio")
    var bio: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    ) : UserDetails {

    // 3. email 프로퍼티를 직접 사용하므로, 별도의 getter는 삭제한다.

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val pets: MutableList<Pet> = mutableListOf()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val cares: MutableList<Care> = mutableListOf()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val adoptions: MutableList<Adoption> = mutableListOf()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val notifications: MutableList<Notification> = mutableListOf()

    //userDetail 오버라이딩 (이제 _가 없는 프로퍼티를 가리킨다)
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + role.name))
    }

    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    // 비즈니스 로직
    fun updateInfo(name: String, phone: String?, address: String?, bio: String?) {
        this.name = name
        this.phone = phone
        this.address = address
        this.bio = bio
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun updateRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }
}