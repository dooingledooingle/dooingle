package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.dooinglecount.model.QDooingleCount
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QSocialUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class DooingleCountQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : DooingleCountQueryDslRepository {

    private val socialUser = QSocialUser.socialUser
    private val dooingleCount = QDooingleCount.dooingleCount

    override fun getHighCountDooinglers(size: Long): List<DooinglerResponse> {

        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                socialUser.userLink,
                socialUser.nickname
            )
        )
            .from(dooingleCount)
            .leftJoin(dooingleCount.owner, socialUser)
            .orderBy(dooingleCount.count.desc(), dooingleCount.owner.id.asc())
            .limit(size)
            .fetch()
    }

}