package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QUser
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    private val dooingle = QDooingle.dooingle
    private val owner = QUser("ow")

    override fun getDooinglePageable(cursor: Long?, pageable: Pageable): Slice<DooingleResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    DooingleResponse::class.java,
                    owner.name,
                    dooingle.id,
                    dooingle.content,
                    dooingle.createdAt
                )
            )
            .from(dooingle)
            .join(dooingle.owner, owner) // fetchJoin() 사용하면 에러 발생
            .where(lessThanCursor(cursor))
            .orderBy(dooingle.id.desc())
            .limit(10) // TODO 10을 변수에 저장할 방법 생각하기
            .fetch()
            .let { SliceImpl(it) }
    }

    private fun lessThanCursor(cursor: Long?) = cursor?.let { dooingle.id.lt(it) }
    // = if (cursor == null) null else dooingle.id.lt(cursor)
}
