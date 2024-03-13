package com.dooingle.global.exception.custom

import com.dooingle.global.exception.ErrorCode

abstract class RestApiException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
