package com.dooingle.domain.notification.repository

import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.model.QNotification
import com.dooingle.domain.user.model.QSocialUser
import com.dooingle.domain.user.model.SocialUser
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class NotificationQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NotificationQueryDslRepository{
    private val notification = QNotification.notification
    private val dooingle = QDooingle.dooingle
    private val user = QSocialUser.socialUser
    private val owner = QSocialUser("ow")

    override fun getNotificationBySlice(
        user: SocialUser,
        cursor: Long?,
        pageable: Pageable
    ): Slice<NotificationResponse> {
        val selectSize = pageable.pageSize + 1
        val whereClause = BooleanBuilder()
            .and(lessThanCursor(cursor))
            .and(notification.user.eq(user))
        val list = getContents(whereClause, selectSize.toLong())

        return SliceImpl(
            if (list.size < selectSize) list else list.dropLast(1),
            pageable,
            hasNextSlice(list, selectSize))
    }

    private fun lessThanCursor(cursor: Long?) = cursor?.let { notification.id.lt(it) }

    private fun hasNextSlice(list: List<NotificationResponse>, selectSize: Int) = (list.size == selectSize)

    private fun getContents(whereClause: BooleanBuilder, selectSize: Long) =
        queryFactory
            .select(
                Projections.constructor(
                    NotificationResponse::class.java,
                    notification.notificationType.stringValue(),
                    notification.resourceId,
                    owner.userLink,
                )
            ).from(notification)
            .join(notification.user, user)
            .join(dooingle).on(dooingle.id.eq(notification.resourceId))
            .join(owner).on(owner.eq(dooingle.owner))
            .where(whereClause)
            .orderBy(notification.id.desc())
            .limit(selectSize)
            .fetch()
}
