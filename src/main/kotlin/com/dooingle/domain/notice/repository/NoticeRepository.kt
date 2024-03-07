package com.dooingle.domain.notice.repository

import com.dooingle.domain.notice.model.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<Notice, Long>, NoticeQueryDslRepository