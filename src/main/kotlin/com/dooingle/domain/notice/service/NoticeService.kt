package com.dooingle.domain.notice.service

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.repository.NoticeRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val socialUserRepository: SocialUserRepository,
    private val noticeRepository: NoticeRepository
) {
    fun addNotice(userId: Long, request: AddNoticeRequest): Long {
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw Exception("") // TODO

        val notice = noticeRepository.save(request.to(user = user))
        return notice.id!!
    }

}
