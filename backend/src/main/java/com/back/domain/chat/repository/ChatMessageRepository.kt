package com.back.domain.chat.repository

import com.back.domain.chat.entity.ChatMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.sentAt DESC")
    fun findByRoomIdOrderBySentAtDesc(@Param("roomId") roomId: Long, pageable: Pageable): Page<ChatMessage>
}