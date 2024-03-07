package com.dooingle.domain.dooingle.dto

import java.time.ZonedDateTime

data class DooingleAndCatchResponse(
    val ownerName: String,
    val dooingleId: Long,
    val content: String,
    val catch: Any?,
    val createdAt: ZonedDateTime
)