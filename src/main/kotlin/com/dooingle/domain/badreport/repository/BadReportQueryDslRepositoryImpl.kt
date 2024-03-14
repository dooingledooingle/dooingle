package com.dooingle.domain.badreport.repository

import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.model.QBadReport
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.catchdomain.model.QCatch
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.user.model.QSocialUser
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.PredicateOperation
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.ZonedDateTime

class BadReportQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val entityManager: EntityManager
) : BadReportQueryDslRepository {

    private val badReport = QBadReport.badReport
    private val socialUser = QSocialUser.socialUser
    private val dooingle = QDooingle.dooingle
    private val catch = QCatch("catch")

    override fun findBadReportPage(
        reportedTargetType: ReportedTargetType,
        pageRequest: PageRequest,
    ): Page<BadReportResponse> {

        val totalCountOfCorrespondingType = queryFactory
            .select(badReport.count())
            .from(badReport)
            .where(badReport.reportedTargetType.eq(reportedTargetType))
            .fetchFirst()

        return when (reportedTargetType) {
            ReportedTargetType.DOOINGLE -> findDooingleBadReportPage(pageRequest, totalCountOfCorrespondingType)
            ReportedTargetType.CATCH -> findCatchBadReportPage(pageRequest, totalCountOfCorrespondingType)
        }
    }

    private fun findDooingleBadReportPage(
        pageRequest: PageRequest,
        totalCountOfDooingleReport: Long,
    ): Page<BadReportResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    BadReportResponse::class.java,
                    badReport.id,
                    badReport.reporter.id,
                    badReport.reporter.nickname,
                    badReport.reportedTargetType,
                    badReport.reportedTargetId,
                    dooingle.content,
                    badReport.reportReason,
                )
            )
            .from(badReport)
            .join(badReport.reporter, socialUser)
            .join(dooingle).on(badReport.reportedTargetId.eq(dooingle.id))
            .orderBy(badReport.id.desc())
            .limit(pageRequest.pageSize.toLong())
            .fetch()
            .let { PageImpl(it, pageRequest, totalCountOfDooingleReport) }
    }

    private fun findCatchBadReportPage(
        pageRequest: PageRequest,
        totalCountOfCatchReport: Long,
    ): Page<BadReportResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    BadReportResponse::class.java,
                    badReport.id,
                    badReport.reporter.id,
                    badReport.reporter.nickname,
                    badReport.reportedTargetType,
                    badReport.reportedTargetId,
                    catch.content,
                    badReport.reportReason,
                )
            )
            .from(badReport)
            .join(badReport.reporter, socialUser)
            .join(catch).on(badReport.reportedTargetId.eq(catch.id))
            .orderBy(badReport.id.desc())
            .limit(pageRequest.pageSize.toLong())
            .fetch()
            .let { PageImpl(it, pageRequest, totalCountOfCatchReport) }
    }

    override fun updateReportedDooingles(dooingleIdList: List<Long>) {
        queryFactory
            .update(dooingle)
            .set(dooingle.blockedAt, ZonedDateTime.now())
            .where(dooingle.id.`in`(dooingleIdList).and(dooingle.blockedAt.isNull))
            .execute()

        //DB와 영속성 컨텍스트 동기화
        entityManager.flush()
        entityManager.clear()
    }

    override fun updateReportedCatches(catchIdList: List<Long>) {
        queryFactory
            .update(catch)
            .set(catch.blockedAt, ZonedDateTime.now())
            .where(catch.id.`in`(catchIdList).and(catch.blockedAt.isNull))
            .execute()

        //DB와 영속성 컨텍스트 동기화
        entityManager.flush()
        entityManager.clear()
    }
}
