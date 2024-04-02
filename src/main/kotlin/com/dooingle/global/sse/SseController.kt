package com.dooingle.global.sse

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@RestController
class SseController(
    private val sseEmitters: SseEmitters
) {

    // 알림 구독 예시. text/event-stream 형태로 이벤트 전달해야 함.
    @GetMapping(value = ["/connect"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(): ResponseEntity<SseEmitter> {
        val emitter = SseEmitter(60 * 1000L) // 만료 시간 설정 (기본 30초). 만료 시간 되면 브라우저에서 자동으로 서버에 재연결 요청
        sseEmitters.add(emitter) // 향후 이벤트 발생 시 클라이언트로 이벤트 전송하기 위해 서버에 저장
        // emitter 생성 후 만료 시간까지 아무 데이터도 보내지 않으면 브라우저에서 재연결 요청시 503 Service Unavailable 에러 발생하므로
        // 처음 연결 시 더미 데이터 전달
        try {
            emitter.send(
                SseEmitter.event()
                    .name("test-connect") // 클라이언트에서 해당 이름의 이벤트를 받을 수 있음
                    .data("connected!") // 더미 데이터
            )
        } catch (e: IOException) {
            throw RuntimeException(e)   // TODO: CustomException 생성
        }
        return ResponseEntity.ok(emitter)
    }

    // 예시 메서드
    @PostMapping("/test")
    fun test(): ResponseEntity<Void> {
        sseEmitters.test()
        return ResponseEntity.ok().build()
    }
}