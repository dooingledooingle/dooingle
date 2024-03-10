package com.dooingle.domain.badreport.service

import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.badreport.repository.BadReportRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class BadReportService(
    private val badReportRepository: BadReportRepository
) {

    fun addReport() {
        badReportRepository.save(TODO())
    }

    fun getBadReportPagedList(reportedTargetType: ReportedTargetType, pageRequest: PageRequest): Page<BadReportResponse>? {
        return badReportRepository.findBadReportPage(reportedTargetType, pageRequest)
    }
}
