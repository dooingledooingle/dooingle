package com.dooingle.global.oauth2.client.kakao.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserPropertiesResponse (
    val nickname: String,
    val profileImage: String
)
