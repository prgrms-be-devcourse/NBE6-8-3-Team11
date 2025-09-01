package com.back.domain.chat.service

import com.back.domain.chat.dto.ChatMessageDto
import com.back.domain.chat.dto.response.ChatMessageResponseDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RedisSubscriber(
    private val redisMessageListenerContainer: RedisMessageListenerContainer,
    private val messagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) {
    fun subscribeToChatRoom(roomId: Long?) {
        val channel = "chat:room:$roomId"
        val topic = ChannelTopic(channel)

        redisMessageListenerContainer.addMessageListener(MessageListener { message: Message, pattern: ByteArray? ->
            try {
                // byte[]를 String으로 변환 후 JSON 역직렬화
                val messageBody = message.getBody()
                val jsonString = String(messageBody)
                val chatMessageDto = objectMapper.readValue<ChatMessageDto>(jsonString, ChatMessageDto::class.java)

                val response = ChatMessageResponseDto(
                    messageId = chatMessageDto.id,
                    roomId = chatMessageDto.chatRoomId,
                    senderId = chatMessageDto.senderId,
                    senderName = chatMessageDto.senderName,
                    content = chatMessageDto.content,
                    sentAt = chatMessageDto.sentAt
                )

                messagingTemplate.convertAndSend("/topic/chat/$roomId", response)
                log.info("Message sent to room {}: {}", roomId, response.content)
            } catch (e: Exception) {
                log.error("Error processing message for room {}: {}", roomId, e.message, e)
            }
        }, topic)

        log.info("Subscribed to chat room: {}", roomId)
    }

    fun unsubscribeFromChatRoom(roomId: Long?) {
        val channel = "chat:room:$roomId"
        val topic = ChannelTopic(channel)
        redisMessageListenerContainer.removeMessageListener(null, topic)
        log.info("Unsubscribed from chat room: {}", roomId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RedisSubscriber::class.java)
    }
}