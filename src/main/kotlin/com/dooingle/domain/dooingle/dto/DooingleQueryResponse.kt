package com.dooingle.domain.dooingle.dto

import com.dooingle.domain.catch.dto.CatchResponse
import com.dooingle.domain.catch.model.Catch
import java.time.ZonedDateTime

// 단순히 QueryDsl에 활용하기 위한 DTO
data class DooingleQueryResponse(
    val ownerName: String,
    val dooingleId: Long,
    val content: String,
    val createdAt: ZonedDateTime
) {
        fun toDooingleAndCatchResponse(catch: Catch?): DooingleAndCatchResponse {
        return DooingleAndCatchResponse(
            ownerName = ownerName,
            dooingleId = dooingleId,
            content = content,
            catch = (catch)?.let {
                if (it.deletedAt == null)
                    CatchResponse.from(it)
                else
                    "삭제된 캐치입니다."
            },
            createdAt = createdAt
        )
    }
}