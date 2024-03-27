package com.dooingle.domain.notification.service

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.model.Notification
import com.dooingle.domain.notification.model.NotificationType
import com.dooingle.domain.notification.repository.NotificationRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.sse.SseEmitters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class NotificationService(
    private val sseEmitters: SseEmitters,
    private val socialUserRepository: SocialUserRepository,
    private val notificationRepository: NotificationRepository
) {
    companion object {
        const val NOTIFICATION_PAGE_SIZE = 10
    }

    fun connect(userId: Long): SseEmitter {
        return sseEmitters.addWith(userId)
    }

    fun addDooingleNotification(user: SocialUser, dooingleResponse: DooingleResponse) {
        saveAndSendNotification(user, NotificationType.DOOINGLE, dooingleResponse.dooingleId)
            .also { sseEmitters.sendNewFeedNotification(dooingleResponse) }
    }

    fun addCatchNotification(user: SocialUser, dooingleId: Long) {
        saveAndSendNotification(user, NotificationType.CATCH, dooingleId)
    }

    fun saveAndSendNotification(user: SocialUser, type: NotificationType, dooingleId: Long) {
        notificationRepository.save(
            Notification(
                user = user,
                notificationType = type,
                resourceId = dooingleId
            )
        ).let {
            sseEmitters.sendUserNotification(
                userId = user.id!!,
                data = NotificationResponse.from(it)
            )
        }
    }

    fun getNotifications(userId: Long, cursor: Long?): Slice<NotificationResponse> {
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        val pageRequest = PageRequest.ofSize(NOTIFICATION_PAGE_SIZE)

        return notificationRepository.getNotificationBySlice(user, cursor, pageRequest)
    }

}
