package com.dooingle.global.exception.custom

import com.dooingle.global.exception.CommonErrorCode

data class SocialUserNotFoundByUserLinkException(
    val userLink: String?,

) : RestApiException(CommonErrorCode.MODEL_NOT_FOUND) {

    override val message: String =
        userLink?.let { "SocialUser (userLink: ${userLink}) 가 존재하지 않습니다." } ?: "SocialUser 이 존재하지 않습니다."

}
