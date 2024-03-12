package com.dooingle.domain.notification.dto

import com.dooingle.domain.notification.model.NotificationType

data class NotificationQueryResponse(
    val notificationType: NotificationType,
    val cursor: Long
) {
    companion object {
        private const val NEW_DOOINGLE = "새로운 뒹글이 굴러왔어요!"
        private const val NEW_CATCH = "내가 굴린 뒹글에 캐치가 달렸어요!"

        fun toResponse(notificationQueryResponse: NotificationQueryResponse) = NotificationResponse(
            message = when (notificationQueryResponse.notificationType) {
                NotificationType.DOOINGLE -> NEW_DOOINGLE
                NotificationType.CATCH -> NEW_CATCH
            },
            cursor = notificationQueryResponse.cursor
        )
    }
}
