package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooinglerResponse
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : DooingleQueryDslRepository {

    private val user = QUser.user
    private val dooingle = QDooingle.dooingle

    override fun getHotDooinglerList(): List<DooinglerResponse> {

        return queryFactory.select(
            Projections.constructor(
            DooinglerResponse::class.java,
                user.id,
                user.name
            )
        )
            .from(dooingle)
            .leftJoin(dooingle.owner, user)
            .where(dooingle.createdAt.after(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)))
            .groupBy(dooingle.owner.id)
            .orderBy(dooingle.guest.count().desc())
            .limit(5)
            .fetch()
    }

}