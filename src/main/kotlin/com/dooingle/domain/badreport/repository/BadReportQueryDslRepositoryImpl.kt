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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class BadReportQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
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
}
