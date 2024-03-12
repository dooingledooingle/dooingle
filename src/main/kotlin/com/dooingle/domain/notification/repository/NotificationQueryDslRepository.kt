package com.dooingle.domain.notification.repository

import com.dooingle.domain.notification.dto.NotificationQueryResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface NotificationQueryDslRepository {
    fun getNotificationBySlice(user: SocialUser, cursor: Long?, pageable: Pageable): Slice<NotificationQueryResponse>
}