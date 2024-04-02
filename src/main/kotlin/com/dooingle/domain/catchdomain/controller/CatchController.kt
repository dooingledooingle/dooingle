package com.dooingle.domain.catchdomain.controller

import com.dooingle.domain.catchdomain.dto.AddCatchRequest
import com.dooingle.domain.catchdomain.dto.CatchResponse
import com.dooingle.domain.catchdomain.service.CatchService
import com.dooingle.global.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dooingles/{dooingleId}/catches")
class CatchController(
    private val catchService: CatchService
) {
    // 캐치 생성
    @PostMapping
    fun addCatch(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable dooingleId: Long,
        @RequestBody addCatchRequest: AddCatchRequest
    ): ResponseEntity<CatchResponse> {
        val response: CatchResponse = catchService.addCatch(dooingleId, userPrincipal.id, addCatchRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }
    
    // 캐치 삭제
    @DeleteMapping("/{catchId}")
    fun deleteCatch(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable dooingleId: Long,
        @PathVariable catchId: Long
    ): ResponseEntity<Unit> {
        catchService.deleteCatch(dooingleId, catchId, userPrincipal.id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}