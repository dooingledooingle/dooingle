package com.dooingle.domain.badreport.repository

import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.model.ReportedTargetType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface BadReportQueryDslRepository {

    fun findBadReportPage(reportedTargetType: ReportedTargetType, pageRequest: PageRequest): Page<BadReportResponse>

    fun updateReportedDooingles(dooingleIdList:List<Long>)

    fun updateReportedCatches(catchIdList:List<Long>)
}
