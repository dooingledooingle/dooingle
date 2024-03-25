package com.dooingle.domain.user.controller

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.ProfileImageUrlResponse
import com.dooingle.domain.user.dto.ProfileResponse
import com.dooingle.domain.user.dto.UpdateProfileDto
import com.dooingle.domain.user.service.SocialUserService
import com.dooingle.global.exception.custom.NotPermittedException
import com.dooingle.global.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
class UserController(
    private val socialUserService: SocialUserService
) {
    @GetMapping
    fun getDooinglerList(@RequestParam condition: String?): ResponseEntity<List<DooinglerResponse>> {
        return ResponseEntity.ok().body(socialUserService.getDooinglerList(condition))
    }

    @PatchMapping(value = ["/{userId}/profile"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal,
                      @PathVariable userId:Long,
                      @RequestPart(value = "request") @Valid request: UpdateProfileDto,
                      @RequestPart(value = "img", required = false) img:MultipartFile?)
    : ResponseEntity<UpdateProfileDto> {
        if(userPrincipal.id != userId) throw NotPermittedException(userId = userPrincipal.id, modelName = "User", modelId = userPrincipal.id)

        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.updateProfile(userId, request, img))
    }

    @GetMapping("/profile")
    fun getProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal) : ResponseEntity<ProfileResponse>{
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.getProfile(userPrincipal.id))
    }

    @GetMapping("/{userLink}/profile-image")
    fun getProfileImageByUserLink(@PathVariable userLink: String) : ResponseEntity<ProfileImageUrlResponse>{
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.getProfileImageUrlByUserLink(userLink))
    }

    @GetMapping("/current-dooingler")
    fun getCurrentDooingler(@AuthenticationPrincipal userPrincipal: UserPrincipal) : ResponseEntity<DooinglerResponse>{
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.getCurrentDooingler(userPrincipal.id))
    }
}