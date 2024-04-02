package com.dooingle.global.exception.custom

import com.dooingle.global.exception.CommonErrorCode

data class ConflictStateException(
    val customMessage: String?
) : RestApiException(CommonErrorCode.CONFLICT_STATE) {

    override val message: String = customMessage ?: super.errorCode.message
}
