package com.dooingle.domain.notice.repository

import com.dooingle.domain.notice.dto.NoticeResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface NoticeQueryDslRepository {
    fun findAllNoticeList(pageable: Pageable): Page<NoticeResponse>
}