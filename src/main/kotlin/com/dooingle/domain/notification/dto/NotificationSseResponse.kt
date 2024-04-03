package com.dooingle.domain.notification.dto

import com.dooingle.domain.notification.model.Notification

data class NotificationSseResponse(
    val notificationType: String,
    val cursor: Long,
) {
    companion object {
        fun from(notification: Notification) = NotificationSseResponse(
            notificationType = notification.notificationType.toString(),
            cursor = notification.resourceId + 1,
        )
    }
}
