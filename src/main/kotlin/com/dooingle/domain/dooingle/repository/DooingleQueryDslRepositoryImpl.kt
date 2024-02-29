package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QUser
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
    private val guest = QUser("gu")

    override fun getDooinglePageable(cursor: Long?, pageable: Pageable): Slice<Dooingle> {
        return queryFactory
            .selectFrom(dooingle)
            .join(dooingle.owner, owner).fetchJoin()
            .join(dooingle.guest, guest).fetchJoin()
            .where(lessThanCursor(cursor))
            .orderBy(dooingle.id.desc())
            .limit(10) // TODO 10을 변수에 저장할 방법 생각하기
            .fetch()
            .let { SliceImpl(it) }
    }

    private fun lessThanCursor(cursor: Long?): BooleanExpression?
        = if (cursor == null) null else dooingle.id.lt(cursor)
}
