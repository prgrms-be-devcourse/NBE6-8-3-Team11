package com.back.domain.notification.service

import com.back.domain.member.entity.Member
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import com.back.domain.notification.dto.response.NotificationResponseDto
import com.back.domain.notification.entity.Notification
import com.back.domain.notification.enums.NotificationType
import com.back.domain.notification.repository.NotificationRepository
import com.back.global.exception.CustomException
import com.back.global.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    
    companion object {
        private val log = LoggerFactory.getLogger(NotificationService::class.java)
    }

    @Transactional(readOnly = true)
    fun getNotificationsList(memberEmail: String): List<NotificationResponseDto> {
        val member = memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        val notifications = notificationRepository.findByMemberOrderByCreatedAtDesc(member)
        return notifications.map { NotificationResponseDto.from(it) }
    }

    fun deleteNotification(memberEmail: String?, notificationId: Long) {
        val member = memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        val notification = notificationRepository.findByIdAndMember(notificationId, member)
            ?: throw CustomException(ErrorCode.NOTI_NOT_FOUND)

        notificationRepository.delete(notification)
    }

    fun deleteAllNotification(username: String?) {
        val member = memberRepository.findByEmail(username)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        val notifications = notificationRepository.findByMember(member)
        if (notifications.isNotEmpty()) {
            notificationRepository.deleteAll(notifications)
        }
    }

    /**
     * 실시간 알림 전송 - 개별 사용자
     */
    fun sendRealTimeNotification(recipientId: Long, type: NotificationType, title: String, message: String) {
        // DB에 알림 저장
        val member = memberRepository.findById(recipientId)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        val notification = Notification.create(
            type = type,
            title = title,
            message = message,
            member = member
        )

        notificationRepository.save(notification)

        // WebSocket으로 실시간 전송
        val notificationDto = NotificationResponseDto.from(notification)
        val destination = "/queue/notifications/${member.id}"
        messagingTemplate.convertAndSend(destination, notificationDto)

        log.info("Real-time notification sent to user: {}", member.name)
    }

    /**
     * 채팅방에 알림 전송
     */
    fun sendMessageToUserEnterChatRoom(opponent: Member) {
        val message = "상대방이 채팅방에 입장했습니다."
        sendRealTimeNotification(opponent.id!!, NotificationType.NEW_MESSAGE, "채팅방 입장 알림", message)
    }

    /**
     * 입양 신청 알림 전송
     */
    fun sendAdoptionRequestNotification(recipientId: Long, title: String, requesterName: String) {
        val message = "${requesterName}님이 입양을 신청했습니다."
        sendRealTimeNotification(recipientId, NotificationType.ADOPTION_REQUESTED, title, message)
    }

    /**
     * 돌봄 신청 알림 전송
     */
    fun sendCareRequestNotification(recipientId: Long, title: String, requesterName: String?) {
        val message = "${requesterName}님이 돌봄을 신청했습니다."
        sendRealTimeNotification(recipientId, NotificationType.CARE_REQUESTED, title, message)
    }

    /**
     * 돌봄 승인/거절 알림 전송
     */
    fun sendResponseNotification(recipientId: Long, title: String, type: String, isAccepted: Boolean) {
        val notificationType = when (type) {
            "ADOPTION" -> if (isAccepted) NotificationType.ADOPTION_ACCEPTED else NotificationType.ADOPTION_REJECTED
            else -> if (isAccepted) NotificationType.CARE_ACCEPTED else NotificationType.CARE_REJECTED
        }
        val message = "신청이 ${if (isAccepted) "승인" else "거절"}되었습니다."
        sendRealTimeNotification(recipientId, notificationType, title, message)
    }

    /**
     * 채팅방 생성 알림 전송
     */
    fun sendChatNotification(recipientId: Long, senderName: String, title: String) {
        val message = "${senderName}님이 채팅방을 생성했습니다."
        sendRealTimeNotification(recipientId, NotificationType.NEW_MESSAGE, title, message)
    }

    /**
     * 채팅 메시지 알림 전송
     */
    fun sendChatDeleteNotification(recipientId: Long, title: String) {
        val message = "채팅방이 삭제되었습니다."
        sendRealTimeNotification(recipientId, NotificationType.CHAT_ROOM_DELETED, title, message)
    }
}
