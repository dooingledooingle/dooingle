package com.dooingle.global.exception

import com.dooingle.global.exception.custom.RestApiException
import com.dooingle.global.sse.SseEmitters
import jakarta.servlet.http.HttpServletResponse
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler(
    private val sseEmitters: SseEmitters
) : ResponseEntityExceptionHandler() {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) // SSE 관련 에러를 회피하고 로그만 남기기 위해 두는 로거

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error(e.message, e)
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

    @ExceptionHandler(ClientAbortException::class)
    fun handleClientAbortException(exception: Exception, response: HttpServletResponse) {
        sseEmitters.completeAllEmitters()
        // spring sse exception handling로 구글링 중 https://amaran-th.github.io/Spring/[Spring]%20Server-Sent%20Events(SSE)/
        // 여기서 힌트를 얻어서 이 에러가 발생하면 emitter 모두 끝내버림
        // 리액트 엄격 모드 때문이 아니더라도 운영 환경에도 발생할 수 있는 에러라고 생각하여 처리할 필요가 있다고 생각했음

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "text/event-stream"
        
        // response.writer.write("event: error\ndata: ${exception.message}\n\n")
        // TODO 이 부분은 나중에 다시 생각해봐야할 듯하다.
        //  2024-03-27T16:53:04.602+09:00  WARN 34744 --- [nio-8080-exec-3] .m.m.a.ExceptionHandlerExceptionResolver : Failure in @ExceptionHandler com.dooingle.global.exception.GlobalExceptionHandler#handleClientAbortException(Exception, HttpServletResponse)
        //  java.lang.IllegalStateException: getOutputStream() has already been called for this response
        //  결국 위 에러는 해결하진 않고 로그만 찍고 회피함
        logger.error(exception.message, exception)
        response.flushBuffer()
    }
}
