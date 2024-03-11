package com.dooingle.domain.notification.repository

import com.dooingle.domain.notification.model.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
}