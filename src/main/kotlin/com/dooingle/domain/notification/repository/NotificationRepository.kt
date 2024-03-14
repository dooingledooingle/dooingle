package com.dooingle.domain.notification.repository

import com.dooingle.domain.notification.model.Notification
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long>, NotificationQueryDslRepository {
    fun findAllByUser(user: SocialUser): List<Notification>
}