package com.dooingle.global.exception.custom

import com.dooingle.global.exception.CommonErrorCode

data class InvalidParameterException(
    val customMessage: String?
): RestApiException(CommonErrorCode.INVALID_PARAMETER) {

    override val message: String = customMessage ?: super.errorCode.message
}