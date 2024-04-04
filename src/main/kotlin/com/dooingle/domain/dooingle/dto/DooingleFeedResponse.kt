package com.dooingle.domain.dooingle.dto

import java.time.ZonedDateTime

data class DooingleFeedResponse(
    val ownerName: String,
    val ownerUserLink: String,
    val dooingleId: Long,
    val content: String?,
    val hasCatch: Boolean,
    val createdAt: ZonedDateTime,
    val blockedAt: ZonedDateTime?,
)
