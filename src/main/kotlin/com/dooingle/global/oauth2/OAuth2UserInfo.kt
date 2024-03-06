package com.dooingle.global.oauth2

import com.dooingle.global.oauth2.provider.OAuth2Provider

open class OAuth2UserInfo (
    val provider: OAuth2Provider,
    val id: String,
    val nickname: String,
    val profileImage: String
)
