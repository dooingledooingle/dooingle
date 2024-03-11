package com.dooingle.domain.badreport.dto

import com.dooingle.domain.badreport.model.ReportedTargetType

data class BadReportResponse(
    val id : Long,
    val reporterId: Long,
    val reporterName: String,
    val reportedTargetType: ReportedTargetType,
    val reportedTargetId: Long,
    val reportedTargetContent: String,
    val reportReason: String,
)
