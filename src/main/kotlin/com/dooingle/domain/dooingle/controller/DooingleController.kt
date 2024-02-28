package com.dooingle.domain.dooingle.controller

import com.dooingle.domain.dooingle.dto.DooinglerResponse
import com.dooingle.domain.dooingle.service.DooingleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DooingleController(
    private val dooingleService: DooingleService
) {
    @GetMapping("/users")
    fun GetDooinglerList(@RequestParam(required = false) condition: String?): ResponseEntity<List<DooinglerResponse>> {
        return ResponseEntity.ok().body(dooingleService.getDooinglerList(condition))
    }

}