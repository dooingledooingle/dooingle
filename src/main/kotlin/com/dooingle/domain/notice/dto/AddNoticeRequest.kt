package com.dooingle.domain.notice.dto

import com.dooingle.domain.notice.model.Notice
import com.dooingle.domain.user.model.User

data class AddNoticeRequest(
    val title: String,
    val content: String
) {
    fun to(user: User) = Notice(title = title, content = content, user = user)
}