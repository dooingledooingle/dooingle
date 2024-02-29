package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/dooingles")
@RestController
class DooingleFeedController(
    private val dooingleService: DooingleService,
) {

    @GetMapping
    fun getDooingleFeeds(cursor: Long?): ResponseEntity<Slice<DooingleResponse>> {
        val pageRequest = PageRequest.ofSize(PAGE_SIZE)
        return ResponseEntity.ok(dooingleService.getDooingleFeeds(cursor, pageRequest))
    }

    // TODO 팔로우 기능 구현 후 구현 필요
//    @GetMapping("/follow")
//    fun getDooingleFeedsOfFollows(cursor: Long): ResponseEntity<Slice<DooingleResponse>> {
//        val pageRequest = PageRequest.ofSize(PAGE_SIZE)
//        return ResponseEntity.ok(dooingleService.getDooingleFeedsOfFollows(cursor, pageRequest))
//    }

    companion object {
        const val PAGE_SIZE = 10
    }
}
