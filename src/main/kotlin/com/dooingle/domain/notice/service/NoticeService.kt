package com.dooingle.domain.notice.service

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.dto.NoticeResponse
import com.dooingle.domain.notice.model.Notice
import com.dooingle.domain.notice.repository.NoticeRepository
import com.dooingle.domain.user.model.UserRole
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.NotPermittedException
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
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        check(user.role == UserRole.ADMIN) { throw NotPermittedException(userId = userId, modelName = "Notice", null) }

        val notice = noticeRepository.save(request.to(user = user))
        return notice.id!!
    }

    @Transactional
    fun updateNotice(userId: Long, noticeId: Long, request: AddNoticeRequest) {
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)

        val notice = getNotice(noticeId)

        if (user.id != notice.user.id) {
            throw NotPermittedException(
                userId = user.id!!,
                modelName = "Notice", modelId = noticeId)
        }

        notice.update(request)
    }

    @Transactional
    fun deleteNotice(userId: Long, noticeId: Long) {
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)

        val notice = getNotice(noticeId)

        if (user.id != notice.user.id) {
            throw NotPermittedException(
                userId = user.id!!,
                modelName = "Notice", modelId = noticeId)
        }

        notice.updateForDelete()
    }

    fun findNotice(noticeId: Long): NoticeResponse {
        val notice = getNotice(noticeId)

        return NoticeResponse.from(notice)
    }

    fun findAllNotices(page: Int): Page<NoticeResponse> {
        val pageable = PageRequest.of(page - 1, NOTICE_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))

        return noticeRepository.findAllNoticeList(pageable)
    }

    private fun getNotice(noticeId: Long): Notice {
        return noticeRepository.findByIdOrNull(noticeId)
            ?: throw ModelNotFoundException(modelName = "Notice", modelId = noticeId)
    }

}
