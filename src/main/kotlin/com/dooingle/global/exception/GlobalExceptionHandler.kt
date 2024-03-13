package com.dooingle.global.exception

import com.dooingle.global.exception.custom.RestApiException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<ErrorResponse> {
        return handleException(CommonErrorCode.INTERNAL_SERVER_ERROR, e.message)
    }

    @ExceptionHandler(RestApiException::class)
    fun handleRestApiException(e: RestApiException): ResponseEntity<ErrorResponse> {
        return handleException(e.errorCode, e.message)
    }

    private fun handleException(errorCode: ErrorCode, message: String?): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(ErrorResponse(code = errorCode.name, message = message))
    }

}