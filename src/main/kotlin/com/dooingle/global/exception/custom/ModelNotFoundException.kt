package com.dooingle.global.exception.custom

import com.dooingle.global.exception.CommonErrorCode

data class ModelNotFoundException(
    val modelName: String,
    val modelId: Long?,

) : RestApiException(CommonErrorCode.MODEL_NOT_FOUND) {

    override val message: String =
        modelId?.let { "$modelName (id: ${modelId}) 가 존재하지 않습니다." } ?: "$modelName 이 존재하지 않습니다."

}
