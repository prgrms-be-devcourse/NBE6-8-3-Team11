package com.back.domain.chat.entity

import com.back.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "chat_message")
@EntityListeners(AuditingEntityListener::class)
class ChatMessage private constructor(
    @field:Column(name = "chat_message_content", nullable = false)
    @field:Lob
    var content: String?,
    
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "chat_room_id")
    var chatRoom: ChatRoom,
    
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "sender_id")
    var sender: Member
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    var id: Long? = null
        private set

    @CreatedDate
    @Column(updatable = false)
    var sentAt: LocalDateTime? = LocalDateTime.now()
        private set

    companion object {
        fun create(
            content: String?,
            chatRoom: ChatRoom,
            sender: Member
        ): ChatMessage {
            return ChatMessage(
                content = content,
                chatRoom = chatRoom,
                sender = sender
            )
        }
    }
}
