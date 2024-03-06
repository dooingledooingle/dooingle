package com.dooingle.domain.dooingle.dto

import com.dooingle.domain.dooingle.model.Dooingle
import java.time.ZonedDateTime

data class DooingleResponse(
    val ownerName: String,
    val dooingleId: Long,
    val content: String,
    val createdAt: ZonedDateTime
) {
    companion object {
        fun from(dooingle: Dooingle): DooingleResponse {
            return DooingleResponse(
                ownerName = dooingle.owner.name,
                dooingleId = dooingle.id!!,
                content = dooingle.content,
                createdAt = dooingle.createdAt
            )
        }
    }
}