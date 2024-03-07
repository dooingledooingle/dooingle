package com.dooingle.domain.notice.service

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.dto.NoticeResponse
import com.dooingle.domain.notice.model.Notice
import com.dooingle.domain.notice.repository.NoticeRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeService(
    private val socialUserRepository: SocialUserRepository,
    private val noticeRepository: NoticeRepository
) {
    companion object {
        const val NOTICE_PAGE_SIZE = 10
    }

    fun addNotice(userId: Long, request: AddNoticeRequest): Long {
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw Exception("") // TODO

        val notice = noticeRepository.save(request.to(user = user))
        return notice.id!!
    }

    @Transactional
    fun updateNotice(userId: Long, noticeId: Long, request: AddNoticeRequest) {
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw Exception("")
        val notice = getNotice(noticeId)

        notice.update(request)
    }

    @Transactional
    fun deleteNotice(userId: Long, noticeId: Long) {
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw Exception("")
        val notice = getNotice(noticeId)

        notice.updateForDelete()
    }

    fun findNotice(userId: Long, noticeId: Long): NoticeResponse{
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw Exception("")
        val notice = getNotice(noticeId)

        return NoticeResponse.from(notice)
    }

    fun findAllNotices(userId: Long, page: Int): Page<NoticeResponse> {
        val pageable = PageRequest.of(page - 1, NOTICE_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))

        return noticeRepository.findAllNoticeList(pageable)
    }

    private fun getNotice(noticeId: Long): Notice {
        return noticeRepository.findByIdOrNull(noticeId)?: throw Exception("")
    }


}
