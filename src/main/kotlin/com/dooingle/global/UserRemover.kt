package com.dooingle.global

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity
import java.util.concurrent.atomic.AtomicLong

@RestController
class UserRemover(
    private val socialUserRepository: SocialUserRepository,
    private val restClient: RestClient = RestClient.create(),
    @Value("\${oauth2.kakao.admin_key}") private val adminKey: String,
) {
    private val removalWorkCount: AtomicLong = AtomicLong(0)

    @GetMapping("/unlink-all-user")
    fun removeAllUser(): String {
        val usersProviderIdList = getAllUsers().map {
            it.providerId
        }

        usersProviderIdList.forEach {
            removeEachUser(it)
        }

        return "OK"
    }

    private fun removeEachUser(userProviderId: String) {
        val currentWorkCount = removalWorkCount.incrementAndGet()
        println("현재 ${currentWorkCount}번째 작업 시작")

        // 참고: 카카오 로그인 - REST API - 연결 끊기: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink
        try {
            val requestBody = LinkedMultiValueMap<String, String>()
            requestBody.add("target_id_type", "user_id")
            requestBody.add("target_id", userProviderId)

            val response: ResponseEntity<KakaoResponse> = restClient.post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "KakaoAK $adminKey")
                .body(requestBody)
                .retrieve()
                .toEntity<KakaoResponse>()

            response.body?.let { println("${it.id} 삭제 성공") }
        } catch (e: Exception) {
            println("삭제 실패: $userProviderId")
        } finally {
            println("현재 ${currentWorkCount}번째 작업 종료")
        }
    }

    private fun getAllUsers(): MutableIterable<SocialUser> = socialUserRepository.findAll()
}

data class KakaoResponse(
    val id: Long,
)
