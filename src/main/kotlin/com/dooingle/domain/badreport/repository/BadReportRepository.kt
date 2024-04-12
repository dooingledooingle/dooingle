package com.dooingle.domain.badreport.repository

import com.dooingle.domain.badreport.model.BadReport
import com.dooingle.domain.badreport.model.ReportedTargetType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BadReportRepository : JpaRepository<BadReport, Long>, BadReportQueryDslRepository {

    fun findByReportedTargetTypeAndReportedTargetId(reporterTargetType: ReportedTargetType, reporterTargetId: Long): List<BadReport>

}
