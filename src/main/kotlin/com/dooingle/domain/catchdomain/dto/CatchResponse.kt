package com.dooingle.domain.catchdomain.dto

import com.dooingle.domain.catchdomain.model.Catch
import java.time.ZonedDateTime

data class CatchResponse(
    val catchId: Long,
    val content: String?,
    val createdAt: ZonedDateTime,
    val deletedAt: ZonedDateTime? = null,
){
    companion object {
        fun from(catch: Catch): CatchResponse {
            return CatchResponse(
                catchId = catch.id!!,
                content = catch.content,
                createdAt = catch.createdAt,
            )
        }
    }
}
