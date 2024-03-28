package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QSocialUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class SocialUserQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialUserQueryDslRepository {

    private val socialUser = QSocialUser.socialUser

    override fun getNewDooinglers(size: Long): List<DooinglerResponse> {
        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                socialUser.userLink,
                socialUser.nickname
            )
        )
            .from(socialUser)
            .orderBy(socialUser.id.desc())
            .limit(size)
            .fetch()
    }

    override fun getDooingler(userId: Long): DooinglerResponse {
        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                socialUser.userLink,
                socialUser.nickname
            )
        ).from(socialUser).where(socialUser.id.eq(userId)).fetchFirst()
    }
}