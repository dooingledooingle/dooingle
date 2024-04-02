package com.dooingle.global.oauth2.client.kakao.dto

import com.dooingle.domain.user.dto.OAuth2UserInfo
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserInfoResponse(
    id: Long,
    properties: KakaoUserPropertiesResponse,
    kakaoAccount: KakaoUserAccountResponse
) : OAuth2UserInfo(
    provider = OAuth2Provider.KAKAO,
    id = id.toString(),
    nickname = properties.nickname,
    profileImage = if (!kakaoAccount.profile.isDefaultImage) properties.profileImage else null
) {
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserPropertiesResponse (
    val nickname: String,
    val profileImage: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserAccountResponse(
    val profile: KakaoUserProfileResponse
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class KakaoUserProfileResponse(
    val isDefaultImage: Boolean
)
