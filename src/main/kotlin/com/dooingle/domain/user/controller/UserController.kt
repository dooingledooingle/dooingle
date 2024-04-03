package com.dooingle.domain.user.controller

import com.dooingle.domain.user.dto.*
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

    @GetMapping("/search")
    fun searchDooinglers(@RequestParam nickname: String): ResponseEntity<List<SearchDooinglerResponse>> {
        return ResponseEntity.ok().body(socialUserService.searchDooinglers(nickname))
    }

    @PatchMapping(value = ["/profile"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal,
                      @RequestPart(value = "request") @Valid request: UpdateProfileDto,
                      @RequestPart(value = "img", required = false) img:MultipartFile?)
    : ResponseEntity<UpdateProfileDto> {
        // API 호출 url에서 userId 제거하며 주석 처리 
        // if(userPrincipal.id != userId) throw NotPermittedException(userId = userPrincipal.id, modelName = "User", modelId = userPrincipal.id)

        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.updateProfile(userPrincipal.id, request, img))
    }

    @GetMapping("/profile")
    fun getProfile(@AuthenticationPrincipal userPrincipal: UserPrincipal) : ResponseEntity<ProfileResponse>{
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.getProfile(userPrincipal.id))
    }

    @GetMapping("/{userLink}/profile")
    fun getOtherUserProfile(@PathVariable userLink: String) : ResponseEntity<ProfileResponse>{
        return ResponseEntity.status(HttpStatus.OK).body(socialUserService.getOtherUserProfile(userLink))
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