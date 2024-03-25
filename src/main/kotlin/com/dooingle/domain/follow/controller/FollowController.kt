package com.dooingle.domain.follow.controller

import com.dooingle.domain.follow.dto.FollowResponse
import com.dooingle.domain.follow.dto.IsFollowingUserResponse
import com.dooingle.domain.follow.service.FollowService
import com.dooingle.global.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/follow")
class FollowController(
    private val followService: FollowService
) {
    @Operation(summary = "팔로우 등록")
    @PostMapping("/{toUserLink}")
    fun follow(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable toUserLink: String,
    ) : ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.follow(toUserLink, userPrincipal.id))
    }

    @GetMapping("/{toUserLink}")
    fun isFollowingUser(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable toUserLink: String,
    ) : ResponseEntity<IsFollowingUserResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.isFollowingUser(toUserLink, userPrincipal.id))
    }

    @Operation(summary = "내 팔로우 목록 조회")
    @GetMapping
    fun showFollowingList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ) : ResponseEntity<List<FollowResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.showFollowingList(userPrincipal.id))
    }

    @Operation(summary = "내 팔로워 수 조회")
    @GetMapping("/number")
    fun showFollowersNumber(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ) : ResponseEntity<Int>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(followService.showFollowersNumber(userPrincipal.id))
    }

    @Operation(summary = "팔로우 취소")
    @DeleteMapping("/{toUserLink}")
    fun cancelFollowing(
        @PathVariable toUserLink: String,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ) : ResponseEntity<Unit> {
        followService.cancelFollowing(toUserLink, userPrincipal.id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}