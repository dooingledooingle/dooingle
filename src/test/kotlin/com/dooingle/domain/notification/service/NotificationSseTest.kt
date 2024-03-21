package com.dooingle.domain.notification.service

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.model.NotificationType
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.jwt.JwtHelper
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.sse.SseEmitters
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources.createFactory
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class NotificationSseTest(
    private val jwtHelper: JwtHelper,
    private val socialUserRepository: SocialUserRepository,
    private val dooingleRepository: DooingleRepository
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
        eventWrapper.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE
    }

    @Test
    @Throws(InterruptedException::class, JSONException::class, IOException::class)
    fun `특정 유저가 SSE 연결된 경우 해당 유저에게 뒹글이 굴러오면 알림이 전달된다`() {
        // GIVEN
        socialUserRepository.save(userA)
        val connectRequest = generateConnectRequest(jwtHelper.generateAccessToken(userA.id!!, userA.role.toString()))

        socialUserRepository.save(userB)
        val addDooingleRequest = generateAddDooingleRequest(
            token = jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()),
            userId = userA.id!!
        )

        // WHEN
        val eventWrapper = EventSourceWrapper()
        factory.newEventSource(connectRequest, eventWrapper.listener)

        val addDooingleResponse = getResponse(addDooingleRequest)

        // THEN
        eventWrapper.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE

        addDooingleResponse.code shouldBe HttpStatus.CREATED.value()
        val dooingleId = addDooingleResponse.body.string()
            .substringAfter("dooingleId\":").substringBefore(",").toLong()
        val notificationString = objectMapper.writeValueAsString(
            NotificationResponse(
                notificationType = NotificationType.DOOINGLE.toString(),
                cursor = dooingleId
            )
        )
        eventWrapper.receivedData[1] shouldBe notificationString
    }

    @Test
    @Throws(InterruptedException::class, JSONException::class, IOException::class)
    fun `특정 유저가 SSE 연결된 경우 해당 유저가 남긴 뒹글에 캐치가 달리면 알림이 전달된다`() {
        // GIVEN
        socialUserRepository.save(userA)
        socialUserRepository.save(userB)
        val dooingle = dooingleRepository.save(Dooingle(guest = userA, owner = userB, content = "질문입니다.", catch = null))

        val connectRequestOfA = generateConnectRequest(jwtHelper.generateAccessToken(userA.id!!, userA.role.toString()))
        val addCatchRequest = generateAddCatchRequest(
            token = jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()),
            dooingleId = dooingle.id!!
        )

        // WHEN
        val eventWrapperOfA = EventSourceWrapper()
        factory.newEventSource(connectRequestOfA, eventWrapperOfA.listener)

        val addCatchResponse = getResponse(addCatchRequest)

        // THEN
        eventWrapperOfA.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE

        addCatchResponse.code shouldBe HttpStatus.CREATED.value()

        val notificationString = objectMapper.writeValueAsString(
            NotificationResponse(
                notificationType = NotificationType.CATCH.toString(),
                cursor = dooingle.id!!
            )
        )
        eventWrapperOfA.receivedData[1] shouldBe notificationString
    }

    @Test
    @Throws(InterruptedException::class, JSONException::class, IOException::class)
    fun `여러 브라우저가 SSE 연결된 경우 새 뒹글이 등록되면 모든 브라우저에 뒹글이 전달된다`() {

    }

    private fun generateConnectRequest(token: String): Request {
        return Request.Builder()
            .url("http://localhost:$port/api/notifications/connect")
            .header("Cookie", "accessToken=$token")
            .build()
    }

    @Throws(IOException::class)
    private fun getResponse(request: Request): Response {
        val call = client.newCall(request)
        val response = call.execute()
        return response
    }

    @Throws(JSONException::class)
    private fun generateAddDooingleRequest(token: String, userId: Long): Request {
        json.put("content", "질문입니다.")
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return Request.Builder()
            .url("http://localhost:$port/api/users/$userId/dooingles")
            .header("Cookie", "accessToken=$token")
            .post(requestBody)
            .build()
    }

    @Throws(JSONException::class)
    private fun generateAddCatchRequest(token: String, dooingleId: Long): Request {
        json.put("content", "답변입니다.")
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return Request.Builder()
            .url("http://localhost:$port/api/dooingles/$dooingleId/catches")
            .header("Cookie", "accessToken=$token")
            .post(requestBody)
            .build()
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2")

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
        private val json = JSONObject()
        private val objectMapper = ObjectMapper()
    }

}