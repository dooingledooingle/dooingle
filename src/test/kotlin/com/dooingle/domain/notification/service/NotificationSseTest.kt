package com.dooingle.domain.notification.service

import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.notification.dto.NotificationResponse
import com.dooingle.domain.notification.model.NotificationType
import com.dooingle.domain.notification.repository.NotificationRepository
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
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
    private val dooingleRepository: DooingleRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountRepository: DooingleCountRepository,
    private val notificationRepository: NotificationRepository,
    private val sseEmitters: SseEmitters
) {

    @AfterEach
    fun clearData() {
        sseEmitters.completeAllEmitters()
        notificationRepository.deleteAll()
        dooingleCountRepository.deleteAll()
        catchRepository.deleteAll()
        dooingleRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    @Throws(InterruptedException::class)
    fun `특정 유저 접속 브라우저에서 SSE 연결`() {
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
    fun `SSE 연결된 유저에게 뒹글 등록 시 알림 전달`() {
        // GIVEN
        socialUserRepository.save(userA)
        val connectRequest = generateConnectRequest(jwtHelper.generateAccessToken(userA.id!!, userA.role.toString()))

        socialUserRepository.save(userB)
        val addDooingleRequest = generateAddDooingleRequest(
            token = jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()),
            userLink = userA.userLink
        )

        // WHEN
        val eventWrapper = EventSourceWrapper()
        factory.newEventSource(connectRequest, eventWrapper.listener)

        val addDooingleResponse = getResponse(addDooingleRequest)

        // THEN
        eventWrapper.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE

        addDooingleResponse.code shouldBe HttpStatus.CREATED.value()
        val dooingleString = addDooingleResponse.body.string()

//        val notificationString = objectMapper.writeValueAsString(
//            NotificationResponse(
//                notificationType = NotificationType.DOOINGLE.toString(),
//                cursor = dooingleString.substringAfter("dooingleId\":").substringBefore(",").toLong()
//            )
//        )
//        eventWrapper.receivedData[1] shouldBe notificationString // 유저 알림

        eventWrapper.receivedData[2] shouldBe dooingleString // 피드 알림
    }

    @Test
    @Throws(InterruptedException::class, JSONException::class, IOException::class)
    fun `SSE 연결된 유저가 남겼던 뒹글에 캐치 등록 시 알림 전달`() {
        // GIVEN
        socialUserRepository.save(userA)
        socialUserRepository.save(userB)
        val dooingle = dooingleRepository.save(Dooingle(guest = userA, owner = userB, content = "질문입니다.", catch = null))

        val connectRequestOfA = generateConnectRequest(jwtHelper.generateAccessToken(userA.id!!, userA.role.toString()))
        val addCatchRequestOfB = generateAddCatchRequest(
            token = jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()),
            dooingleId = dooingle.id!!
        )

        // WHEN
        val eventWrapperOfA = EventSourceWrapper()
        factory.newEventSource(connectRequestOfA, eventWrapperOfA.listener)

        val addCatchResponse = getResponse(addCatchRequestOfB)

        // THEN
        eventWrapperOfA.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE

        addCatchResponse.code shouldBe HttpStatus.CREATED.value()

//        val notificationString = objectMapper.writeValueAsString(
//            NotificationResponse(
//                notificationType = NotificationType.CATCH.toString(),
//                cursor = dooingle.id!!
//            )
//        )
//        eventWrapperOfA.receivedData[1] shouldBe notificationString
    }

    @Test
    @Throws(InterruptedException::class, JSONException::class, IOException::class)
    fun `새 뒹글 등록 시 SSE 연결된 모든 브라우저에 알림 전달`() {
        // GIVEN
        socialUserRepository.save(userA)
        val tokenOfA = jwtHelper.generateAccessToken(userA.id!!, userA.role.toString())
        val connectRequestOfA1 = generateConnectRequest(tokenOfA)
        val connectRequestOfA2 = generateConnectRequest(tokenOfA) // 동일 유저가 새로운 브라우저 SSE 연결

        socialUserRepository.save(userB)
        val connectRequestOfB = generateConnectRequest(jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()))
        val addDooingleRequest = generateAddDooingleRequest(
            token = jwtHelper.generateAccessToken(userB.id!!, userB.role.toString()),
            userLink = userA.userLink
        )

        // WHEN
        val eventWrapperOfA1 = EventSourceWrapper()
        factory.newEventSource(connectRequestOfA1, eventWrapperOfA1.listener)

        val eventWrapperOfA2 = EventSourceWrapper()
        factory.newEventSource(connectRequestOfA2, eventWrapperOfA2.listener)

        val eventWrapperOfB = EventSourceWrapper()
        factory.newEventSource(connectRequestOfB, eventWrapperOfB.listener)

        val addDooingleResponse = getResponse(addDooingleRequest)

        // THEN
        eventWrapperOfA1.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE
        eventWrapperOfA2.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE
        eventWrapperOfB.receivedData[0] shouldBe SseEmitters.CONNECTED_MESSAGE

        addDooingleResponse.code shouldBe HttpStatus.CREATED.value()
        val dooingleString = addDooingleResponse.body.string()

//        val notificationString = objectMapper.writeValueAsString(
//            NotificationResponse(
//                notificationType = NotificationType.DOOINGLE.toString(),
//                cursor = dooingleString.substringAfter("dooingleId\":").substringBefore(",").toLong()
//            )
//        )
//        eventWrapperOfA1.receivedData[1] shouldBe notificationString // 유저 알림
//        eventWrapperOfA2.receivedData[1] shouldBe notificationString

        eventWrapperOfB.receivedData[1] shouldBe dooingleString // 피드 알림
        eventWrapperOfA1.receivedData[2] shouldBe dooingleString
        eventWrapperOfA2.receivedData[2] shouldBe dooingleString
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
    private fun generateAddDooingleRequest(token: String, userLink: String): Request {
        json.put("content", "질문입니다.")
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        return Request.Builder()
            .url("http://localhost:$port/api/users/$userLink/dooingles")
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

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "1111111111")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "2222222222")

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
                }

                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    receivedData.add(data)
                }

                override fun onClosed(eventSource: EventSource) {
                    isClosed = true
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    onFailureCalled = true
                    logger.error("!!!!!! event source closed with error")
                    logger.error(t.toString())
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

        private val logger = LoggerFactory.getLogger("~~~~~~~~SSE test Logger~~~~~~~~~~")
    }

}