package com.dooingle.global.oauth2.client

import com.dooingle.global.oauth2.provider.OAuth2Provider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KakaoOAuth2Client(
    @Value("\${oauth2.kakao.client_id}") val clientId: String,
    @Value("\${oauth2.kakao.redirect_url}") val redirectUrl: String,
    @Value("\${oauth2.kakao.auth_server_base_url}") val authServerbaseUrl: String,
    @Value("\${oauth2.kakao.resource_server_base_url}") val resourceServerBaseUrl: String,
) : OAuth2Client {

    override fun generateLoginPageUrl(): String {
        return StringBuilder(authServerbaseUrl)
            .append("/oauth/authorize")
            .append("?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectUrl)
            .append("&response_type=").append("code")
            .toString()
    }

    override fun supports(provider: OAuth2Provider) = (provider == OAuth2Provider.KAKAO)

}