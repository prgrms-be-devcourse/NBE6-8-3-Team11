package com.back.domain.chat.entity

import com.back.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
@EntityListeners(AuditingEntityListener::class)
class ChatRoom private constructor(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "first_member_id")
    var firstMember: Member,
    
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "second_member_id")
    var secondMember: Member
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    var id: Long? = null
        private set

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
        private set

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.ALL], orphanRemoval = true)
    val messages: MutableList<ChatMessage> = mutableListOf()

    companion object {
        fun create(
            firstMember: Member,
            secondMember: Member
        ): ChatRoom {
            return ChatRoom(
                firstMember = firstMember,
                secondMember = secondMember
            )
        }
    }
}
