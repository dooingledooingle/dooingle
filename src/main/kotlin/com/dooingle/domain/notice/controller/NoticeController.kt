package com.dooingle.domain.notice.controller

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addNotice(
        @RequestParam userId: Long, // TODO : @AuthenticationPrincipal 변경
        @RequestBody request: AddNoticeRequest
    ): ResponseEntity<Unit> {
        val id = noticeService.addNotice(userId, request)
        return ResponseEntity.created(URI.create("/api/notices/$id")).build()
    }

}