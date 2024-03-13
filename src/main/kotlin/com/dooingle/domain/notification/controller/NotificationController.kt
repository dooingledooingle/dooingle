package com.dooingle.domain.notification.controller

import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.global.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping(value = ["/connect"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(
        @RequestParam userId: Long, // TODO: @AuthenticationPrincipal 로 바꿔야 함
    ): ResponseEntity<SseEmitter> {
        val emitter = notificationService.connect(userId)
        return ResponseEntity.ok(emitter)
    }

    @Operation(summary = "지난 알림 조회")
    @GetMapping
    fun findPastNotifications(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        cursor: Long?
    ): ResponseEntity<Slice<NotificationResponse>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(notificationService.getNotifications(userPrincipal.id, cursor))
    }

}