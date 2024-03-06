package com.dooingle.global.oauth2

import com.dooingle.global.oauth2.client.OAuth2ClientService
import com.dooingle.global.oauth2.provider.OAuth2Provider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2")
class OAuth2LoginController(
    private val oAuth2ClientService: OAuth2ClientService,
    private val oAuth2LoginService: OAuth2LoginService
) {

    @GetMapping("/login/{provider}")
    fun redirectLoginPage(@PathVariable provider: OAuth2Provider, response: HttpServletResponse) {
        val loginPageUrl = oAuth2ClientService.generateLoginPageUrl(provider)
        response.sendRedirect(loginPageUrl)
    }

    @GetMapping("/callback/{provider}")
    fun callback(
        @PathVariable provider: OAuth2Provider,
        @RequestParam(name = "code") authorizationCode: String
    ): String {
        return oAuth2LoginService.login(provider, authorizationCode)
    }
}