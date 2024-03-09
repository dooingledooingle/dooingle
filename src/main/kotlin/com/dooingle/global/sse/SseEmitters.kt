package com.dooingle.global.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

@Component
class SseEmitters {

    private val logger = LoggerFactory.getLogger("~~~~~~~~Emitter Logger~~~~~~~~~~")

    // 타임아웃/비동기 요청 완료 콜백이 emitter 관리하는 다른 스레드에서 실행되기 때문에 thread-safe 자료구조 사용해야 함
    private val notificationEmitters = ConcurrentHashMap<String, SseEmitter>()

    private val testEmitters: MutableList<SseEmitter> = CopyOnWriteArrayList()

    fun add(emitter: SseEmitter): SseEmitter {
        testEmitters.add(emitter)
        logger.info("new emitter added: {}", emitter)
        logger.info("emitter list size: {}", testEmitters.size)
        logger.info("emitter list: {}", testEmitters)
        emitter.onTimeout { // 타임아웃 발생 시 콜백
            logger.info("onTimeout callback")
            emitter.complete()
        }
        emitter.onCompletion {  // 비동기 요청 완료 시 콜백
            logger.info("onCompletion callback")
            testEmitters.remove(emitter)    // 새로운 emitter 생성하기 때문에 서버에서 기존 emitter 제거
        }

        return emitter
    }

    // 예시 메서드
    fun test() {
        val count = counter.incrementAndGet() // thread-safe하게 숫자 증가
        // List의 모든 emitter로 testData 보냄. SSE 커넥션이 열려있는 모든 클라이언트에게 전달됨.
        testEmitters.forEach(Consumer { emitter: SseEmitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name("test")
                        .data("testData$count")
                )
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        })
    }

    companion object {
        private val counter = AtomicLong()
    }
}
