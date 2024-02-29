package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.model.QDooingle
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    private val dooingle = QDooingle.dooingle

    override fun getDooinglePageable(cursor: Long, pageable: Pageable): Slice<Dooingle> {
        return queryFactory
            .selectFrom(dooingle)
            .where(dooingle.id.lt(cursor))
            .limit(10) // TODO 10을 변수에 저장할 방법 생각하기
            .fetch()
            .let { SliceImpl(it) }
    }
}
