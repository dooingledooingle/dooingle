package com.dooingle.domain.user.controller

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun GetDooinglerList(@RequestParam(required = false) condition: String?): ResponseEntity<List<DooinglerResponse>> {
        return ResponseEntity.ok().body(userService.getDooinglerList(condition))
    }
}