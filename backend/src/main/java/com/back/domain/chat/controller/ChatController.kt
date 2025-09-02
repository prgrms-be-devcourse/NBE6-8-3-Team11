package com.back.domain.chat.controller

import com.back.domain.chat.dto.request.ChatMessageRequestDto
import com.back.domain.chat.dto.response.ChatRoomResponseDto
import com.back.domain.chat.service.ChatService
import com.back.domain.chat.service.RedisSubscriber
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
    private val redisSubscriber: RedisSubscriber
) {
    @MessageMapping("/chat.sendMessage")
    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    fun sendMessage(@Payload chatMessageRequest: ChatMessageRequestDto) {
        log.info("Received message: {}", chatMessageRequest.content)
        chatService.sendMessage(chatMessageRequest)
    }

    @MessageMapping("/chat.addUser")
    @Operation(summary = "채팅방에 사용자 추가", description = "사용자를 채팅방에 추가하고 Redis 채널을 구독합니다.")
    fun addUser(
        @Payload chatMessageRequest: ChatMessageRequestDto,
        headerAccessor: SimpMessageHeaderAccessor,
        @AuthenticationPrincipal userDetails: UserDetails
    ) {
        // 사용자를 WebSocket 세션에 추가 - 이메일로 통일
        headerAccessor.getSessionAttributes()!!.put("username", userDetails.username)
        headerAccessor.getSessionAttributes()!!.put("roomId", chatMessageRequest.roomId)

        // Redis 채널 구독
        redisSubscriber.subscribeToChatRoom(chatMessageRequest.roomId)

        // 채팅방 입장 처리 (상대방에게 알림)
        chatService.enterChatRoom(chatMessageRequest.roomId, chatMessageRequest.senderId)

        // 최근 메시지 전송
        chatService.viewRecentMessagesToUser(userDetails.username, chatMessageRequest.roomId)

        log.info("User {} added to room {}", chatMessageRequest.senderId, chatMessageRequest.roomId)
    }

    @PostMapping
    @Operation(summary = "채팅방 생성", description = "두 회원 간의 채팅방을 생성합니다. 이미 존재하는 경우 해당 채팅방을 반환합니다.")
    fun createChatRoom(
        @RequestParam firstMemberId: Long,
        @RequestParam secondMemberId: Long
    ): ResponseEntity<ApiResponse<ChatRoomResponseDto?>> {
        val chatRoom = chatService.createOrGetChatRoom(firstMemberId, secondMemberId)
        return ResponseEntity.ok(ApiResponse.success(chatRoom))
    }

    @GetMapping
    @Operation(summary = "사용자의 채팅방 목록 조회", description = "현재 로그인한 사용자의 모든 채팅방을 조회합니다.")
    fun getUserChatRooms(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> {
        val chatRooms = chatService.getUserChatRooms(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(chatRooms))
    }

    @GetMapping("/{chatroomId}")
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지를 조회합니다.")
    fun getRecentMessagesFromRedis(
        @AuthenticationPrincipal userDetails: UserDetails, @PathVariable chatroomId: Long?
    ): ResponseEntity<ApiResponse<List<Any>>> {
        val recent = chatService.getRecentMessagesFromRedis(chatroomId)
        return ResponseEntity.ok(ApiResponse.success(recent))
    }

    @DeleteMapping("/{chatroomId}")
    @Operation(summary = "채팅방 삭제", description = "특정 채팅방을 삭제합니다.")
    fun deleteChatRoom(@PathVariable chatroomId: Long): ResponseEntity<ApiResponse<Void?>> {
        chatService.deleteChatRoom(chatroomId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatController::class.java)
    }
}