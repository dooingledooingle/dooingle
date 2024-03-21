package com.dooingle.domain.notification.service

import com.dooingle.global.jwt.JwtHelper
import io.kotest.matchers.shouldBe
import okhttp3.*
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources.createFactory
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class NotificationSseTest(
    private val jwtHelper: JwtHelper
) {
    @Test
    @Throws(InterruptedException::class)
    fun `특정 유저가 접속한 브라우저에서 SSE 연결을 요청하면 connect 이벤트의 데이터가 전달된다`() {
        // GIVEN
        val userId: Long = 1
        val role = "USER"
        val request = generateConnectRequest(jwtHelper.generateAccessToken(userId, role))

        // WHEN
        val eventWrapper = EventSourceWrapper()
        factory.newEventSource(request, eventWrapper.listener)

        Thread.sleep(300) // 연결 후 데이터 받을 수 있도록 대기

        // THEN
        eventWrapper.receivedData[0] shouldBe "connected"
    }

    private fun generateConnectRequest(token: String): Request {
        return Request.Builder()
            .url("http://localhost:$port/api/notifications/connect")
            .header("Cookie", "accessToken=$token")
            .build()
    }

    private inner class EventSourceWrapper {
        val listener: EventSourceListener
        val receivedData: MutableList<String> = ArrayList()
        var isOpened: Boolean = false
        var isClosed: Boolean = false
        var onFailureCalled: Boolean = false

        init {
            this.listener = object : EventSourceListener() {
                override fun onOpen(eventSource: EventSource, response: Response) {
                    isOpened = true
                    println("!!!!!!!!!!!!open!!!!!!!!!!!!!!")
                }

                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    receivedData.add(data)
                }

                override fun onClosed(eventSource: EventSource) {
                    isClosed = true
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    onFailureCalled = true
                    println("!!!!!!!!!!!!failure!!!!!!!!!!!!!!")
                    println(eventSource.toString())
                    println(t.toString())
                    println(response.toString())
                }
            }
        }
    }

    @LocalServerPort
    private var port = 0

    companion object {
        private val client = OkHttpClient()
        private val factory: EventSource.Factory = createFactory(client)
    }

}