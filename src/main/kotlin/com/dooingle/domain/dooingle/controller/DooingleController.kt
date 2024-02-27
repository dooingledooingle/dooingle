package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/users/{userId}/dooingles")
class DooingleController(
    private val dooingleService: DooingleService
) {

    // 뒹글 생성
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

    // 단일 뒹글 조회
    @GetMapping("/{dooingleId}")
    fun getDooingle(
        @PathVariable userId: Long,
        @PathVariable dooingleId: Long
    ) : ResponseEntity<DooingleResponse> {
        val response: DooingleResponse = dooingleService.getDooingle(userId, dooingleId)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

}