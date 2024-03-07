package com.dooingle.domain.user.controller

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.UpdateProfileRequest
import com.dooingle.domain.user.dto.UpdateProfileResponse
import org.springframework.http.HttpStatus
import com.dooingle.domain.user.service.SocialUserService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
class UserController(
    private val socialUserService: SocialUserService
) {
    @GetMapping
    fun GetDooinglerList(@RequestParam condition: String?): ResponseEntity<List<DooinglerResponse>> {
        return ResponseEntity.ok().body(socialUserService.getDooinglerList(condition))
    }

    @PatchMapping(value = ["/{userId}/profile"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun UpdateProfile(@PathVariable userId:Long,
                      @RequestPart(value = "request") @Valid request: UpdateProfileRequest,
                      @RequestPart(value = "img", required = false) img:MultipartFile?)
    : ResponseEntity<UpdateProfileResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.updateProfile(userId, request, img))
    }
}