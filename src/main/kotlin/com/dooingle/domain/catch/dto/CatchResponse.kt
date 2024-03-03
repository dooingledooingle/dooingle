package com.dooingle.domain.catch.dto

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.dooingle.model.Dooingle
import java.time.ZonedDateTime

data class CatchResponse(
    val dooingleId: Long,
    val catchId: Long,
    val content: String,
    val createdAt: ZonedDateTime
){
    companion object {
        fun from(catch: Catch, dooingle: Dooingle): CatchResponse {
            return CatchResponse(
                dooingleId = dooingle.id!!,
                catchId = catch.id!!,
                content = catch.content,
                createdAt = catch.createdAt
            )
        }
    }
}
