package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QUser
import com.dooingle.global.property.DooinglersProperties
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class UserQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val dooinglerListProperties: DooinglersProperties
) : UserQueryDslRepository {

    private val user = QUser.user

    override fun getNewDooinglers(): List<DooinglerResponse> {
        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                user.id,
                user.name
            )
        )
            .from(user)
            .orderBy(user.id.desc())
            .limit(dooinglerListProperties.new.toLong())
            .fetch()
    }

}