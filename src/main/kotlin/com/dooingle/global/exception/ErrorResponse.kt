package com.dooingle.global.exception

data class ErrorResponse(
    val code: String,
    val message: String?
) {
    constructor(code: ErrorCode) : this(code.name, code.message)

}
