package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.service.SocialUserService
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class SocialUserRepositoryTest(
    private val socialUserRepository: SocialUserRepository
) {

    @AfterEach
    fun clearData() {
        socialUserRepository.deleteAll()
    }

    @Test
    fun `최근 가입한 순으로 유저 조회`() {
        // GIVEN
        val userList = socialUserRepository.saveAll(userList)
        val size = SocialUserService.NEW_DOOINGLERS_SIZE

        // WHEN
        val result = socialUserRepository.getNewDooinglers(size)

        // THEN
        result.size shouldBe size

        val usersSorted = userList.sortedByDescending { it.id }
        result.zip(usersSorted) { response, entity ->
            response.userLink == entity.userLink && response.nickname == entity.nickname
        }
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "1111111111")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "2222222222")
    private val userC = SocialUser(nickname = "C", provider = OAuth2Provider.KAKAO, providerId = "3", userLink = "3333333333")
    private val userD = SocialUser(nickname = "D", provider = OAuth2Provider.KAKAO, providerId = "4", userLink = "4444444444")
    private val userE = SocialUser(nickname = "E", provider = OAuth2Provider.KAKAO, providerId = "5", userLink = "5555555555")
    private val userF = SocialUser(nickname = "F", provider = OAuth2Provider.KAKAO, providerId = "6", userLink = "6666666666")
    private val userList = listOf(userA, userB, userC, userD, userE, userF)

}