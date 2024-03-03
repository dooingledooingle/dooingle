package com.dooingle.domain.catch.controller

import com.dooingle.domain.catch.dto.AddCatchRequest
import com.dooingle.domain.catch.dto.CatchResponse
import com.dooingle.domain.catch.dto.DeleteCatchRequest
import com.dooingle.domain.catch.service.CatchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @PathVariable dooingleId: Long,
        @RequestBody addCatchRequest: AddCatchRequest
    ): ResponseEntity<CatchResponse> {
        val response: CatchResponse = catchService.addCatch(dooingleId, addCatchRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }
    
    // 캐치 삭제
    @DeleteMapping("/{catchId}")
    fun deleteCatch(
        @PathVariable dooingleId: Long,
        @PathVariable catchId: Long,
        @RequestBody deleteCatchRequest: DeleteCatchRequest
    ): ResponseEntity<Unit> {
        catchService.deleteCatch(dooingleId, catchId, deleteCatchRequest)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}