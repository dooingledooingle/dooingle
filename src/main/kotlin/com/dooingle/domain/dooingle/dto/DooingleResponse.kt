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
                ownerName = dooingle.owner.nickname,
                dooingleId = dooingle.id!!,
                content = dooingle.content,
                createdAt = dooingle.createdAt
            )
        }
    }

//    fun toDooingleAndCatchResponse(catch: Catch?): DooingleAndCatchResponse {
//        return DooingleAndCatchResponse(
//            ownerName = ownerName,
//            dooingleId = dooingleId,
//            content = content,
//            catch = (catch)?.let {
//                if (it.deletedAt == null)
//                    CatchResponse.from(it)
//                else
//                    "삭제된 캐치입니다."
//            },
//            createdAt = createdAt
//        )
//    }
}