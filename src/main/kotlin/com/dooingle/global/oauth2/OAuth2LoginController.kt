package com.dooingle.global.oauth2

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2")
class OAuth2LoginController {

    @GetMapping("/login/{provider}")
    fun redirectLoginPage(@PathVariable provider: OAuth2Provider, response: HttpServletResponse) {
        TODO("리소스 제공자 로그인 페이지 URI로 redirect")
    }

    @GetMapping("/callback/{provider}")
    fun callback(
        @PathVariable provider: OAuth2Provider,
        @RequestParam(name = "code") authorizationCode: String
    ): String {
        TODO("인가 코드로 액세스 토큰 얻어 사용자 정보 조회해 회원가입/로그인 후 우리 쪽 액세스 토큰 반환")
    }
}