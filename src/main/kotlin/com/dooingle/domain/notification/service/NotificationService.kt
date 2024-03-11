package com.dooingle.domain.notification.service

import com.dooingle.global.sse.SseEmitters
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class NotificationService(
    private val sseEmitters: SseEmitters
) {
    fun connect(userId: Long): SseEmitter {
        return sseEmitters.addWith(userId)
    }
}