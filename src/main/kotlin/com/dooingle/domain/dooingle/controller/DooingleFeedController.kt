package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/dooingles")
@RestController
class DooingleFeedController(
    private val dooingleService: DooingleService,
) {

    @GetMapping
    fun getDooingleFeeds(): ResponseEntity<Slice<DooingleResponse>> {
        return ResponseEntity.ok(dooingleService.getDooingleFeeds())
    }

    @GetMapping("/follow")
    fun getDooingleFeedsOfFollows(): ResponseEntity<Slice<DooingleResponse>> {
        return ResponseEntity.ok(dooingleService.getDooingleFeedsOfFollows())
    }
}
