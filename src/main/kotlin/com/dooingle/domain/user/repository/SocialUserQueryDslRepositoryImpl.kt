package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.SearchDooinglerResponse
import com.dooingle.domain.user.model.QProfile
import com.dooingle.domain.user.model.QSocialUser
import com.querydsl.core.types.Projections
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

    override fun searchDooinglers(nickname: String): List<SearchDooinglerResponse> {
        return queryFactory.select(
            Projections.constructor(
                SearchDooinglerResponse::class.java,
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