package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/{userId}/dooingles")
class DooingleController(
    private val dooingleService: DooingleService
) {
    // 뒹글 생성
    @Operation(summary = "뒹글을 생성하는 API")
    @PostMapping
    fun addDooingle(
        @PathVariable userId: Long,
        @RequestBody addDooingleRequest: AddDooingleRequest
    ): ResponseEntity<DooingleResponse>{
        val response: DooingleResponse = dooingleService.addDooingle(userId, addDooingleRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    // 개인 뒹글 페이지 조회(뒹글,캐치)
    @Operation(summary = "개인 뒹글 페이지 조회")
    @GetMapping
    fun dooinglePage (
        @PathVariable userId: Long,
        @RequestParam loginUserId: Long, // TODO : 로그인한 유저의 ID (추후 인증/인가 구현되면 변경)
        cursor: Long?
    ) : ResponseEntity<Slice<DooingleAndCatchResponse>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(dooingleService.getPage(userId, loginUserId, cursor))
    }

    // 단일 뒹글 조회(글자수 제한 정책으로 실제 사용되지는 않지만 정책수정을 통한 추가 기능의 확장성을 위해 남겨둠)
    // 추후 사용된다면 ownerId로 해당 유저가 존재하는지 확인하는 로직을 추가
    @Operation(summary = "미사용 API")
    @GetMapping("/{dooingleId}")
    fun getDooingle(
        @PathVariable userId: Long,
        @PathVariable dooingleId: Long
    ) : ResponseEntity<DooingleResponse> {
        val response: DooingleResponse = dooingleService.getDooingle(userId, dooingleId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }

}