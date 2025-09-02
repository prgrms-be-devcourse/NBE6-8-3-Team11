package com.back.domain.notification.controller

import com.back.domain.notification.dto.response.NotificationResponseDto
import com.back.domain.notification.service.NotificationService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "사용자의 모든 알림을 조회합니다.")
    fun getNotificationsList(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<NotificationResponseDto>>> {
        val notifications = notificationService.getNotificationsList(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(notifications))
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "사용자의 알림을 삭제합니다")
    fun deleteNotification(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        notificationService.deleteNotification(userDetails.username, notificationId)
        return ResponseEntity.ok(ApiResponse.success("알림이 삭제되었습니다.", null))
    }

    @DeleteMapping("/all")
    @Operation(summary = "알림 전체 삭제", description = "사용자의 알림을 전체 삭제합니다")
    fun deleteAllNotifications(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        notificationService.deleteAllNotification(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success("알림이 삭제되었습니다.", null))
    }
}
