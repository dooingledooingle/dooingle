package com.dooingle.domain.notice.repository

import com.dooingle.domain.notice.model.Notice
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<Notice, Long>
