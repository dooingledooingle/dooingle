package com.dooingle.domain.notice.repository

import com.dooingle.domain.notice.dto.NoticeResponse
import com.dooingle.domain.notice.model.QNotice
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NoticeQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeQueryDslRepository {
    private val notice = QNotice.notice

    override fun findAllNoticeList(pageable: Pageable) : Page<NoticeResponse> {
        val totalPage = queryFactory.select(notice.count()).from(notice)
            .where(BooleanBuilder().and(notice.deletedAt.isNull)).fetchOne() ?: 0L

        return queryFactory
            .select(
                Projections.constructor(
                    NoticeResponse::class.java,
                    notice.id,
                    notice.title,
                    notice.content,
                    notice.createdAt,
                )
            )
            .from(notice)
            .where(BooleanBuilder().and(notice.deletedAt.isNull))
            .orderBy(notice.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .let { PageImpl(it, pageable, totalPage) }
    }
}