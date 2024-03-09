package com.dooingle.domain.notification.controller

import com.dooingle.global.sse.SseEmitters
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val sseEmitters: SseEmitters
) {

    @GetMapping(value = ["/connect"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(
        @RequestParam userId: Long, // TODO: @AuthenticationPrincipal 로 바꿔야 함
    ): ResponseEntity<SseEmitter> {
        val emitter = SseEmitter() // 만료 시간 설정 (기본 30초). 만료 시간 되면 브라우저에서 자동으로 서버에 재연결 요청
        // 향후 이벤트 발생 시 클라이언트로 이벤트 전송하기 위해 ConcurrentHashMap 에 로그인 유저 아이디를 키로 서버에 저장
        sseEmitters.add(userId, emitter)
        // emitter 생성 후 만료 시간까지 아무 데이터도 보내지 않으면 브라우저에서 재연결 요청시 503 Service Unavailable 에러 발생하므로
        // 처음 연결 시 더미 데이터 전달
        try {
            emitter.send(
                SseEmitter.event()
                    .name("connect") // 클라이언트에서 해당 이름의 이벤트를 받을 수 있음
                    .data("connected!") // 더미 데이터
            )
        } catch (e: IOException) {
            throw RuntimeException(e)   // TODO: CustomException 생성
        }
        return ResponseEntity.ok(emitter)
    }

}