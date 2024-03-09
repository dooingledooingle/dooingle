package com.dooingle.domain.notification.controller

import com.dooingle.domain.notification.service.NotificationService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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

}