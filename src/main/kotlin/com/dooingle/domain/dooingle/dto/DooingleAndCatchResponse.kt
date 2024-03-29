package com.dooingle.domain.dooingle.dto

import com.dooingle.domain.catchdomain.dto.CatchResponse
import java.time.ZonedDateTime

data class DooingleAndCatchResponse(
    val ownerName: String,
    val ownerUserLink: String,
    val dooingleId: Long,
    val content: String,
    val catch: CatchResponse?,
    val createdAt: ZonedDateTime
)