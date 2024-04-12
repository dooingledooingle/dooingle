package com.dooingle.domain.user.controller

import com.dooingle.global.oauth2.provider.OAuth2Provider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
class LogoutController(@Value("\${frontend.domain}") frontendUri: String) {

    private val frontendWelcomePageUri = frontendUri

    @PostMapping("/logout")
    fun redirectLoginPage(response: HttpServletResponse): ResponseEntity<Unit> {
        val accessTokenInCookie = ResponseCookie
            .from("accessToken")
            .httpOnly(true) /* .secure(true) // https 사용할 경우 빌더에 추가 */
            .path("/")
            .maxAge(0)
            .build()

        val headers = HttpHeaders()
            .also { it.location = URI.create(frontendWelcomePageUri) } // 헤더에 리다이렉트 URI 설정
            .also { it.add(HttpHeaders.SET_COOKIE, accessTokenInCookie.toString()) } // 헤더에 SET COOKIE 헤더 추가, 내용은 위에서 만들어놓은 accessTokenInCookie 객체

        return ResponseEntity(headers, HttpStatus.PERMANENT_REDIRECT)
    }
}
