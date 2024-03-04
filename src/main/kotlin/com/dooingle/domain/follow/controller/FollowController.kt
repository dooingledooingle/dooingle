package com.dooingle.domain.follow.controller

import com.dooingle.domain.follow.dto.FollowResponse
import com.dooingle.domain.follow.service.FollowService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/follow")
class FollowController(
    private val followService: FollowService
) {
    @Operation(summary = "팔로우 등록")
    @PostMapping("/{toUserId}")
    fun follow(
        @PathVariable toUserId: Long,
        @RequestParam fromUserId: Long // TODO : 인증/인가 기능 구현되면 추후 수정
    ) : ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.follow(toUserId, fromUserId))
    }

    @Operation(summary = "내 팔로우 목록 조회")
    @GetMapping
    fun showFollowingList(
        @RequestParam userId: Long // TODO : 인증/인가 기능 구현되면 추후 수정
    ) : ResponseEntity<List<FollowResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.showFollowingList(userId))
    }

    @Operation(summary = "내 팔로워 수 조회")
    @GetMapping
    fun showFollowersNumber(
        @RequestParam userId: Long // TODO : 인증/인가 기능 구현되면 추후 수정
    ) : ResponseEntity<Int>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.showFollowersNumber(userId))
    }

    @Operation(summary = "팔로우 취소")
    @DeleteMapping("/{toUserId}")
    fun cancelFollowing(
        @PathVariable toUserId: Long,
        @RequestParam fromUserId: Long // TODO : 인증/인가 기능 구현되면 추후 수정
    ) : ResponseEntity<Unit> {
        followService.cancelFollowing(toUserId, fromUserId)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}