package com.back.domain.chat.service

import com.back.domain.chat.dto.ChatMessageDto
import com.back.domain.chat.dto.request.ChatMessageRequestDto
import com.back.domain.chat.dto.response.ChatMessageResponseDto
import com.back.domain.chat.dto.response.ChatRoomResponseDto
import com.back.domain.chat.entity.ChatMessage
import com.back.domain.chat.entity.ChatRoom
import com.back.domain.chat.repository.ChatMessageRepository
import com.back.domain.chat.repository.ChatRoomRepository
import com.back.domain.member.entity.Member
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import com.back.domain.notification.repository.NotificationRepository
import com.back.domain.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val memberRepository: MemberRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisSubscriber: RedisSubscriber,
    private val notificationRepository: NotificationRepository,
    private val notificationService: NotificationService
) {
    @Transactional
    fun createOrGetChatRoom(member1Id: Long, member2Id: Long): ChatRoomResponseDto {
        val member1 = memberRepository.findById(member1Id)
            .orElseThrow { IllegalArgumentException("Member not found: $member1Id") }
        val member2 = memberRepository.findById(member2Id)
            .orElseThrow { IllegalArgumentException("Member not found: $member2Id") }

        // 기존 채팅방 찾기
        val existingRoom = chatRoomRepository.findByMembers(member1, member2)

        if (existingRoom != null) {
            log.info("Found existing chat room: {}", existingRoom.id)
            return ChatRoomResponseDto.from(existingRoom)
        }

        // 새 채팅방 생성
        val chatRoom = ChatRoom.create(
            firstMember = member1,
            secondMember = member2
        )

        val savedRoom = chatRoomRepository.save(chatRoom)
        log.info("Created new chat room: {}", savedRoom.id)

        // 상대방에게 채팅방 생성 알림 전송 (member1이 요청자라고 가정)
        try {
            notificationService.sendChatNotification(
                member2.id!!, member1.name,
                "새 채팅방이 생성되었습니다"
            )

            notifyChatRoomCreated(savedRoom)
            log.info("New chat room {} created, notified member {}", savedRoom.id, member2.id)
        } catch (e: Exception) {
            log.error("Failed to send notification for new chat room", e)
            // 알림 실패해도 채팅방 생성은 성공으로 처리
        }

        return ChatRoomResponseDto.from(savedRoom)
    }

    private fun notifyChatRoomCreated(chatRoom: ChatRoom) {
        // 두 사용자 모두에게 알림 전송
        messagingTemplate.convertAndSendToUser(
            chatRoom.firstMember.id.toString(),
            "/queue/chat-rooms",
            ChatRoomResponseDto.from(chatRoom)
        )

        messagingTemplate.convertAndSendToUser(
            chatRoom.secondMember.id.toString(),
            "/queue/chat-rooms",
            ChatRoomResponseDto.from(chatRoom)
        )
    }

    fun enterChatRoom(roomId: Long, userId: Long) {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { IllegalArgumentException("Chat room not found: $roomId") }

        val user = memberRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found: $userId") }

        // 상대방 찾기
        val opponent = if (chatRoom.firstMember.id == userId) {
            chatRoom.secondMember
        } else {
            chatRoom.firstMember
        }

        // 상대방에게 채팅방 입장 알림 보내기
        notificationService.sendMessageToUserEnterChatRoom(opponent)

        log.info("User {} entered room {}, notified opponent {}", userId, roomId, opponent.id)
    }

    @Transactional
    fun sendMessage(request: ChatMessageRequestDto): ChatMessage {
        val chatRoom = chatRoomRepository.findById(request.roomId)
            .orElseThrow { IllegalArgumentException("Chat room not found: ${request.roomId}") }

        val sender = memberRepository.findById(request.senderId)
            .orElseThrow { IllegalArgumentException("Sender not found: ${request.senderId}") }

        val message = ChatMessage.create(
            content = request.content,
            chatRoom = chatRoom,
            sender = sender
        )

        val savedMessage = chatMessageRepository.save(message)

        // Redis에 메시지 저장 (최근 100개 메시지, 24시간 유지)
        val redisKey = "chat:room:${request.roomId}:messages"
        val savedMessageDto = ChatMessageDto.from(savedMessage)
        redisTemplate.opsForList().rightPush(redisKey, savedMessageDto) // rightPush로 변경 (시간순 저장)
        redisTemplate.opsForList().trim(redisKey, -100, -1) // 최근 100개만 유지 (오른쪽에서부터)
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS)

        // Redis Pub/Sub으로 메시지 발행 (다른 서버 인스턴스에 전달)
        val channel = "chat:room:${request.roomId}"
        val chatMessageDto = ChatMessageDto.from(savedMessage)
        redisTemplate.convertAndSend(channel, chatMessageDto)

        // WebSocket으로 메시지 전송
        val response = ChatMessageResponseDto(
            messageId = savedMessage.id,
            roomId = savedMessage.chatRoom.id,
            senderId = savedMessage.sender.id,
            senderName = savedMessage.sender.name,
            content = savedMessage.content,
            sentAt = savedMessage.sentAt
        )

        val destination = "/topic/chat/${request.roomId}"
        log.info("Sending WebSocket message to destination: {}, message: {}", destination, response)
        messagingTemplate.convertAndSend(destination, response)

        return savedMessage
    }

    fun getRecentMessagesFromRedis(roomId: Long?): List<Any>? {
        val redisKey = "chat:room:$roomId:messages"
        return redisTemplate.opsForList().range(redisKey, 0, -1)
    }

    fun getUserChatRooms(memberEmail: String?): List<ChatRoomResponseDto> {
        val member = memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }
        val chatRooms = chatRoomRepository.findByMember(member)
        return chatRooms.map { ChatRoomResponseDto.from(it) }
    }

    fun viewRecentMessagesToUser(memberEmail: String?, roomId: Long?) {
        val recentMessages = getRecentMessagesFromRedis(roomId)

        val member = memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        // Redis에서 가져온 메시지들을 ChatMessageResponse로 변환
        val messageResponses = recentMessages?.filterIsInstance<ChatMessageDto>()?.map { messageDto ->
            ChatMessageResponseDto(
                messageId = messageDto.id,
                roomId = messageDto.chatRoomId,
                senderId = messageDto.senderId,
                senderName = messageDto.senderName,
                content = messageDto.content,
                sentAt = messageDto.sentAt
            )
        } ?: emptyList()

        // 사용자에게 최근 메시지 전송 -> roomId를 사용하여 특정 사용자에게 전송
        messagingTemplate.convertAndSend("/queue/user/$roomId/messages", messageResponses)

        log.info("Sent {} recent messages to user {} for room {}", messageResponses.size, member.id, roomId)
    }

    @Transactional
    fun deleteChatRoom(roomId: Long) {
        val chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow { IllegalArgumentException("Chat room not found: $roomId") }

        // Redis에서 채팅방 관련 데이터 삭제
        val redisKey = "chat:room:$roomId:messages"
        redisTemplate.delete(redisKey)

        // Redis Pub/Sub 채널 구독 해제
        redisSubscriber.unsubscribeFromChatRoom(roomId)

        // DB에서 채팅방 삭제 (Cascade로 메시지도 자동 삭제)
        chatRoomRepository.delete(chatRoom)

        // 양쪽 사용자에게 채팅방 삭제 알림 전송 (순차적으로)
        val firstMemberId = chatRoom.firstMember.id
        val secondMemberId = chatRoom.secondMember.id

        // 첫 번째 멤버에게 알림
        notificationService.sendChatDeleteNotification(firstMemberId!!, "채팅방이 삭제되었습니다")

        // 잠시 대기 후 두 번째 멤버에게 알림
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        notificationService.sendChatDeleteNotification(secondMemberId!!, "채팅방이 삭제되었습니다")
        log.info("Chat room {} deleted, cleaned Redis data and notified users", roomId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatService::class.java)
    }
}