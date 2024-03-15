package com.dooingle.domain.notification.dto

import com.dooingle.domain.notification.model.Notification

data class NotificationResponse(
    val notificationType: String,
    val cursor: Long
) {
    companion object {
        fun from(notification: Notification) = NotificationResponse(
            notificationType = notification.notificationType.toString(),
            cursor = notification.resourceId
        )
    }
}