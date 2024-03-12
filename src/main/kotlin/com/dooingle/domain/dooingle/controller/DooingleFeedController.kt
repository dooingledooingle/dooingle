package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import com.dooingle.global.security.UserPrincipal
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/dooingles")
@RestController
class DooingleFeedController(
    private val dooingleService: DooingleService,
) {

    @GetMapping
    fun getDooingleFeed(cursor: Long?): ResponseEntity<Slice<DooingleResponse>> {
        val pageRequest = PageRequest.ofSize(PAGE_SIZE)
        return ResponseEntity.ok(dooingleService.getDooingleFeed(cursor, pageRequest))
    }

    @GetMapping("/follow")
    fun getDooingleFeedOfFollowing(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        cursor: Long?
    ): ResponseEntity<Slice<DooingleResponse>> {
        val pageRequest = PageRequest.ofSize(PAGE_SIZE)
        return ResponseEntity.ok(dooingleService.getDooingleFeedOfFollowing(userPrincipal.id, cursor, pageRequest))
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
