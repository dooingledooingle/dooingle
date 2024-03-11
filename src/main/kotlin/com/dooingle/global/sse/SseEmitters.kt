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
    private val notificationEmitters = ConcurrentHashMap<String, MutableList<SseEmitter>>()

    // ConcurrentHashMap 에 저장
    fun addWith(userId: Long): SseEmitter {
        val emitter = SseEmitter() // 만료 시간 설정 (기본 30초). 만료 시간 되면 브라우저에서 자동으로 서버에 재연결 요청
        // emitter 생성 후 만료 시간까지 아무 데이터도 보내지 않으면 브라우저에서 재연결 요청시 503 Service Unavailable 에러 발생하므로
        // 처음 연결 시 더미 데이터 전달
        emitter.sendData(eventName = "connect", data = "connected")

        // 향후 이벤트 발생 시 클라이언트로 이벤트 전송하기 위해 ConcurrentHashMap 에 로그인 유저 아이디를 키로 서버에 저장
        val key = userId.toString()
        // key(userId)가 존재하면 value 반환하고, 존재하지 않으면 빈 list 생성해 value 에 넣고 반환
        val emitterList = notificationEmitters.getOrPut(key) { CopyOnWriteArrayList() }
        emitterList.add(emitter)
        logger.info("new emitter added: {}", emitter)
        logger.info("emitter map: {}", notificationEmitters)

        emitter.onTimeout { // 타임아웃 발생 시 콜백
            logger.info("onTimeout callback")
            emitter.complete()
        }
        emitter.onCompletion {  // 비동기 요청 완료 시 콜백
            logger.info("onCompletion callback")
            emitterList.remove(emitter)    // 새로운 emitter 생성하기 때문에 서버에서 기존 emitter 제거
            if (emitterList.isEmpty()) {
                notificationEmitters.remove(key)
            }
            logger.info("emitter map: {}", notificationEmitters)
        }

        return emitter
    }

    fun sendNotification(userId: Long, notification: String) {
        notificationEmitters[userId.toString()]?.forEach { emitter ->
            emitter.sendData(eventName = "notification", data = notification)
        }
    }

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
            emitter.sendData(eventName = "test", data = "testData$count")
        })
    }

    companion object {
        private val counter = AtomicLong()
    }
}

fun SseEmitter.sendData(eventName: String, data: Any) {
    try {
        send(
            SseEmitter.event()
                .name(eventName) // 클라이언트에서 해당 이름의 이벤트를 받을 수 있음
                .data(data) // 더미 데이터
        )
    } catch (e: IOException) {
        throw RuntimeException(e)   // TODO: CustomException 생성
    }
}
