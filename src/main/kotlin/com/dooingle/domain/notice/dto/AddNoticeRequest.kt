package com.dooingle.domain.notice.dto

import com.dooingle.domain.notice.model.Notice
import com.dooingle.domain.user.model.SocialUser

data class AddNoticeRequest(
    val title: String,
    val content: String
) {
    fun to(user: SocialUser) = Notice(title = title, content = content, user = user)
}