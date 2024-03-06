package com.dooingle.global.oauth2.client.kakao.dto

import com.dooingle.global.oauth2.OAuth2UserInfo
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserInfoResponse(
    id: Long,
    properties: KakaoUserPropertiesResponse
) : OAuth2UserInfo(
    provider = OAuth2Provider.KAKAO,
    id = id.toString(),
    nickname = properties.nickname,
    profileImage = properties.profileImage
) {
}
