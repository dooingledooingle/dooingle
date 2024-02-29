package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.model.QDooingle
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    private val dooingle = QDooingle.dooingle

    override fun getDooinglePageable(cursor: Long, pageable: Pageable): Dooingle {
        TODO("Not yet implemented")
    }
}
