package com.dooingle.domain.notice.controller

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.dto.NoticeResponse
import com.dooingle.domain.notice.service.NoticeService
import com.dooingle.global.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: AddNoticeRequest
    ): ResponseEntity<Unit> {
        val id = noticeService.addNotice(userPrincipal, request)
        return ResponseEntity.created(URI.create("/api/notices/$id")).build()
    }

    @PutMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateNotice(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable noticeId: Long,
        @RequestBody request: AddNoticeRequest
    ): ResponseEntity<NoticeResponse>{
        noticeService.updateNotice(userPrincipal, noticeId, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteNotice(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable noticeId: Long
    ):ResponseEntity<Unit>{
        noticeService.deleteNotice(userPrincipal,noticeId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "공지 단건 조회")
    @GetMapping("/{noticeId}")
    fun getNotice(
        @PathVariable noticeId: Long
    ):ResponseEntity<NoticeResponse>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.findNotice(noticeId))
    }

    @Operation(summary = "공지 전체목록 조회")
    @GetMapping
    fun getAllNotice(
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<Page<NoticeResponse>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.findAllNotices(page))
    }

}