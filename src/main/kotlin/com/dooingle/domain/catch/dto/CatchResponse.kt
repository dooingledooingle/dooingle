package com.dooingle.domain.catch.dto

import com.dooingle.domain.catch.model.Catch
import java.time.ZonedDateTime

data class CatchResponse(
    val dooingleId: Long,
    val catchId: Long,
    val content: String,
    val createdAt: ZonedDateTime
){
    companion object {
        fun from(catch: Catch): CatchResponse {
            return CatchResponse(
                dooingleId = catch.dooingle.id!!,
                catchId = catch.id!!,
                content = catch.content,
                createdAt = catch.createdAt
            )
        }
    }
}
