package com.dooingle.domain.notification.service

import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.model.Notification
import com.dooingle.domain.notification.model.NotificationType
import com.dooingle.domain.notification.repository.NotificationRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.sse.SseEmitters
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class NotificationService(
    private val sseEmitters: SseEmitters,
    private val notificationRepository: NotificationRepository
) {
    fun connect(userId: Long): SseEmitter {
        return sseEmitters.addWith(userId)
    }

    fun addDooingleNotification(user: SocialUser, dooingleId: Long) {
        addAndSendNotification(user, NotificationType.DOOINGLE, dooingleId)
            .also { sseEmitters.sendNewFeedNotification() }
    }

    fun addCatchNotification(user: SocialUser, dooingleId: Long) {
        addAndSendNotification(user, NotificationType.CATCH, dooingleId)
    }

    fun addAndSendNotification(user: SocialUser, type: NotificationType, dooingleId: Long) {
        notificationRepository.save(
            Notification(
                user = user,
                notificationType = type,
                resourceId = dooingleId
            )
        ).let {
            NotificationResponse.from(it)
        }.let { response ->
            sseEmitters.sendUserNotification(
                userId = user.id!!,
                data = "${response.message}-${response.cursor}"
            )
        }
    }

}