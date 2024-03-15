package com.dooingle.domain.user.controller

import com.dooingle.domain.user.service.OAuth2LoginService
import com.dooingle.global.oauth2.client.OAuth2ClientService
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
@RequestMapping("/oauth2")
class OAuth2LoginController(
    private val oAuth2ClientService: OAuth2ClientService,
    private val oAuth2LoginService: OAuth2LoginService,
    @Value("\${frontend.domain}") frontendUri: String,
) {
    private val frontendFeedPageUri = frontendUri + "feeds"

    @GetMapping("/login/{provider}")
    fun redirectLoginPage(@PathVariable provider: OAuth2Provider, response: HttpServletResponse) {
        val loginPageUrl = oAuth2ClientService.generateLoginPageUrl(provider)
        response.sendRedirect(loginPageUrl)
    }


    @GetMapping("/callback/{provider}")
    fun callback(
        @PathVariable provider: OAuth2Provider,
        @RequestParam(name = "code") authorizationCode: String
    ): ResponseEntity<Unit> {

        val accessToken = oAuth2LoginService.login(provider, authorizationCode)

        val accessTokenInCookie = ResponseCookie
            .from("accessToken", accessToken)
            .httpOnly(true) /* .secure(true) // https 사용할 경우 빌더에 추가 */
            .path("/")
            .maxAge(3600)
            .build()

        val headers = HttpHeaders()
            .also { it.location = URI.create(frontendFeedPageUri) } // 헤더에 리다이렉트 URI 설정
            .also { it.add(HttpHeaders.SET_COOKIE, accessTokenInCookie.toString()) } // 헤더에 SET COOKIE 헤더 추가, 내용은 위에서 만들어놓은 accessTokenInCookie 객체

        return ResponseEntity(headers, HttpStatus.PERMANENT_REDIRECT)
        // 상태코드는 308로 보내고, 위에서 만들어놓은 headers(HttpHeaders 타입 까보면 MultiValueMap 자료구조 구현체)를 헤더로 설정하여 보낸다.
    }

}