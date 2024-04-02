package com.dooingle.domain.notice.dto

import com.dooingle.domain.notice.model.Notice
import java.time.ZonedDateTime

data class NoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: ZonedDateTime
) {
    companion object{
        fun from(notice: Notice):NoticeResponse = NoticeResponse(
            id = notice.id!!,
            title = notice.title,
            content = notice.content,
            createdAt = notice.createdAt
        )
    }
}

