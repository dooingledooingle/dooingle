package com.dooingle.global.oauth2.client

import com.dooingle.global.oauth2.OAuth2UserInfo
import com.dooingle.global.oauth2.provider.OAuth2Provider
import org.springframework.stereotype.Component

@Component
class OAuth2ClientService(
    private val clients: List<OAuth2Client>
) {

    fun generateLoginPageUrl(provider: OAuth2Provider): String {
        val client = this.selectClient(provider)
        return client.generateLoginPageUrl()
    }

    fun getUserInfo(provider: OAuth2Provider, authorizationCode: String): OAuth2UserInfo {
        val client = this.selectClient(provider)
        return client.getAccessToken(authorizationCode)
            .let { client.retrieveUserInfo(it) }
    }

    private fun selectClient(provider: OAuth2Provider): OAuth2Client {
        return clients.find { it.supports(provider) }
            ?: throw RuntimeException("지원하지 않는 OAuth Provider 입니다!") // TODO : customException 생성
    }

}
