package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QDooingleCount
import com.dooingle.domain.user.model.QUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class DooingleCountQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : DooingleCountQueryDslRepository {

    private val user = QUser.user
    private val dooingleCount = QDooingleCount.dooingleCount

    override fun getHighCountDooinglers(): List<DooinglerResponse> {

        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                user.id,
                user.name
            )
        )
            .from(dooingleCount)
            .leftJoin(dooingleCount.owner, user)
            .orderBy(dooingleCount.count.desc())
            .limit(5)
            .fetch()
    }

}