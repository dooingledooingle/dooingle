package com.dooingle.global.oauth2.client

import com.dooingle.global.oauth2.provider.OAuth2Provider

interface OAuth2Client {
    fun generateLoginPageUrl(): String
    fun supports(provider: OAuth2Provider): Boolean
}
