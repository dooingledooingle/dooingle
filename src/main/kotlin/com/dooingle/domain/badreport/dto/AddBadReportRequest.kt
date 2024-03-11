package com.dooingle.domain.badreport.dto

import com.dooingle.domain.badreport.model.BadReport
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.user.model.SocialUser

data class AddBadReportRequest(
    val reportedTargetType: ReportedTargetType,
    val reportedTargetId: Long,
    val reportReason: String,
) {

    fun toEntity(reporter: SocialUser) = BadReport(
        reporter = reporter,
        reportedTargetType = this.reportedTargetType,
        reportedTargetId = this.reportedTargetId,
        reportReason = this.reportReason,
    )
}
