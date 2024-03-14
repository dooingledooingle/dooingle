package com.dooingle.domain.notification.dto

data class NotificationResponse(
    val notificationType: String,
    val cursor: Long
)