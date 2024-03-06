package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QSocialUser
import com.dooingle.global.property.DooinglersProperties
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class SocialUserQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val dooinglerListProperties: DooinglersProperties
) : SocialUserQueryDslRepository {

    private val socialUser = QSocialUser.socialUser

    override fun getNewDooinglers(): List<DooinglerResponse> {
        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                socialUser.id,
                socialUser.nickname
            )
        )
            .from(socialUser)
            .orderBy(socialUser.id.desc())
            .limit(dooinglerListProperties.new.toLong())
            .fetch()
    }

}