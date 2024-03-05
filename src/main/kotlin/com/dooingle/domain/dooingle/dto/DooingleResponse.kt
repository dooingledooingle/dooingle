package com.dooingle.domain.dooingle.dto

import com.dooingle.domain.catch.dto.CatchResponse
import com.dooingle.domain.dooingle.model.Dooingle
import java.time.ZonedDateTime

data class DooingleResponse(
    val ownerName: String,
    val dooingleId: Long,
    val content: String,
    val catch: Any?,
    val createdAt: ZonedDateTime
) {
    companion object {
        fun from(dooingle: Dooingle): DooingleResponse {
            return DooingleResponse(
                ownerName = dooingle.owner.name,
                dooingleId = dooingle.id!!,
                content = dooingle.content,
                catch = (dooingle.catch)?.let {
                    if (it.deletedAt == null)
                        CatchResponse.from(it)
                    else
                        "삭제된 캐치입니다."
                    },
                createdAt = dooingle.createdAt
            )
        }
    }
}