package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.service.DooingleService
import com.dooingle.global.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/my-dooingles")
class MyDooingleController(
    private val dooingleService: DooingleService
) {
    @GetMapping
    fun dooinglePage (
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        cursor: Long?
    ) : ResponseEntity<Slice<DooingleAndCatchResponse>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(dooingleService.getPage(userPrincipal.id, cursor))
    }
}
