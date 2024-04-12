package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.DooinglerWithProfileResponse
import com.dooingle.domain.user.model.QProfile
import com.dooingle.domain.user.model.QSocialUser
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory

class SocialUserQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialUserQueryDslRepository {

    private val socialUser = QSocialUser.socialUser
    private val profile = QProfile.profile

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

    override fun searchDooinglersByNickname(nickname: String): List<DooinglerWithProfileResponse> {
        return queryFactory.select(
            Projections.constructor(
                DooinglerWithProfileResponse::class.java,
                socialUser.nickname,
                socialUser.userLink,
                profile.imageUrl,
                profile.description,
            )
        )
            .from(socialUser)
            .leftJoin(profile).on(profile.user.eq(socialUser))
            .where(socialUser.nickname.contains(nickname))
            .fetch()
    }

    override fun getRandomDooinglers(size: Long): List<DooinglerWithProfileResponse> {
        return queryFactory.select(
            Projections.constructor(
                DooinglerWithProfileResponse::class.java,
                socialUser.nickname,
                socialUser.userLink,
                profile.imageUrl,
                profile.description,
            )
        )
            .from(socialUser)
            .leftJoin(profile).on(profile.user.eq(socialUser))
            .orderBy(Expressions.numberTemplate(Double::class.java, "function('rand')").asc())
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