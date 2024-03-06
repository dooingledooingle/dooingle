package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QUser
import com.dooingle.domain.user.model.User
import com.querydsl.core.BooleanBuilder
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

    override fun getPersonalPageBySlice(owner: User, cursor: Long?, pageable: Pageable): Slice<DooingleResponse> {
        val selectSize = pageable.pageSize + 1
        val whereClause = BooleanBuilder()
            .and(lessThanCursor(cursor))
            .and(dooingle.owner.eq(owner))
        val list = getContents(whereClause, pageable)

        return SliceImpl(
            if (list.size < selectSize) list else list.dropLast(1),
            pageable,
            hasNextSlice(list, selectSize))
    }

    private fun lessThanCursor(cursor: Long?) = cursor?.let { dooingle.id.lt(it) }

    private fun hasNextSlice(dooingleList: List<DooingleResponse>, selectSize: Int) = (dooingleList.size == selectSize)

    private fun getContents(whereClause: BooleanBuilder, pageable: Pageable) =
        queryFactory
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
            .where(whereClause)
            .orderBy(dooingle.id.desc())
            .limit((pageable.pageSize + 1).toLong())
            .fetch()
}
