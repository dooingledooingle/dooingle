package com.dooingle.domain.user.service

import com.dooingle.global.jwt.JwtHelper
import com.dooingle.global.oauth2.client.OAuth2ClientService
import com.dooingle.global.oauth2.provider.OAuth2Provider
import org.springframework.stereotype.Service

@Service
class OAuth2LoginService(
    private val oAuth2ClientService: OAuth2ClientService,
    private val socialUserService: SocialUserService,
    private val jwtHelper: JwtHelper
) {

    fun login(provider: OAuth2Provider, authorizationCode: String): String {
        // 인가코드로 액세스 토큰 얻고, 액세스 토큰으로 사용자 정보 조회
        return oAuth2ClientService.getUserInfo(provider, authorizationCode)
            // 사용자 정보로 social user 없으면 회원가입, 있으면 조회
            .let { socialUserService.registerIfAbsent(it) }
            // social user 정보로 우리 쪽 액세스 토큰 발급
            .let { jwtHelper.generateAccessToken(it.id!!, it.role.toString()) }
    }

}
