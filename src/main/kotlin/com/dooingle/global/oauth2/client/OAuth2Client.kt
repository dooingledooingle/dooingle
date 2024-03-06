package com.dooingle.global.oauth2.client

import com.dooingle.global.oauth2.OAuth2UserInfo
import com.dooingle.global.oauth2.provider.OAuth2Provider

interface OAuth2Client {
    fun generateLoginPageUrl(): String
    fun getAccessToken(authorizationCode: String): String
    fun retrieveUserInfo(accessToken: String): OAuth2UserInfo
    fun supports(provider: OAuth2Provider): Boolean
}
