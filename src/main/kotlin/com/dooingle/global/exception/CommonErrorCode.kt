package com.dooingle.global.exception

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    override val httpStatus: HttpStatus, override val message: String
) : ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "모델이 존재하지 않습니다."),
    NOT_PERMITTED(HttpStatus.FORBIDDEN, "해당 유저는 모델에 대한 권한이 없습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "올바르지 않은 입력입니다."),
    CONFLICT_STATE(HttpStatus.CONFLICT, "현재 상태에서 처리할 수 없는 요청입니다."),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "API 호출 권한이 없습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "JWT 인증에 실패했습니다."),

}