package com.dooingle.global.exception.custom

import com.dooingle.global.exception.CommonErrorCode

data class NotPermittedException(
    val userId: Long,
    val modelName: String,
    val modelId: Long?
) : RestApiException(CommonErrorCode.NOT_PERMITTED) {

    override val message =
        modelId?.let { "User(id: $userId)는 $modelName(id: $modelId)에 대한 권한이 없습니다." }
            ?: "User(id: $userId)는 $modelName 에 대한 권한이 없습니다."
}