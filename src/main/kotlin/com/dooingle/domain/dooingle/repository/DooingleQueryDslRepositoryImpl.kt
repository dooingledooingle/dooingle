package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    private val dooingle = QDooingle.dooingle
    private val owner = QUser("ow")

    override fun getDooinglesBySlice(cursor: Long?, pageable: Pageable): Slice<DooingleResponse> {
        val selectSize = pageable.pageSize + 1

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
            .join(dooingle.owner, owner)
            .where(lessThanCursor(cursor))
            .orderBy(dooingle.id.desc())
            .limit(selectSize.toLong())
            .fetch()
            .let { SliceImpl(it.dropLast(1), pageable, hasNextSlice(it, selectSize)) }
    }

    private fun lessThanCursor(cursor: Long?) = cursor?.let { dooingle.id.lt(it) }

    private fun hasNextSlice(dooingleList: List<DooingleResponse>, selectSize: Int) = (dooingleList.size == selectSize)
}
