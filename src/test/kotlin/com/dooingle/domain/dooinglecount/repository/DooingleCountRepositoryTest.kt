package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.dooinglecount.model.DooingleCount
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
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
class DooingleCountRepositoryTest(
    private val dooingleCountRepository: DooingleCountRepository,
    private val socialUserRepository: SocialUserRepository
) {

    @AfterEach
    fun clearData() {
        dooingleCountRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `뒹글이 많이 등록된 순, 먼저 가입한 순으로 유저 조회`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        val dooingleCountList = dooingleCountRepository.saveAll(dooingleCountList)
        val size = SocialUserService.HOT_DOOINGLERS_SIZE

        // WHEN
        val result = dooingleCountRepository.getHighCountDooinglers(size)

        // THEN
        result.size shouldBe size

        val dooingleCountSorted = dooingleCountList.sortedWith(
            compareByDescending<DooingleCount> { it.count }.thenBy { it.owner.id })
        result.zip(dooingleCountSorted) { response, entity ->
            response.userLink == entity.owner.userLink && response.nickname == entity.owner.nickname
        }
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "1111111111")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "2222222222")
    private val userC = SocialUser(nickname = "C", provider = OAuth2Provider.KAKAO, providerId = "3", userLink = "3333333333")
    private val userD = SocialUser(nickname = "D", provider = OAuth2Provider.KAKAO, providerId = "4", userLink = "4444444444")
    private val userE = SocialUser(nickname = "E", provider = OAuth2Provider.KAKAO, providerId = "5", userLink = "5555555555")
    private val userF = SocialUser(nickname = "F", provider = OAuth2Provider.KAKAO, providerId = "6", userLink = "6666666666")
    private val userList = listOf(userA, userB, userC, userD, userE, userF)

    private val dooingleCountList = listOf(
        DooingleCount(owner = userA, count = 10),
        DooingleCount(owner = userB, count = 9),
        DooingleCount(owner = userC, count = 11),
        DooingleCount(owner = userD, count = 8),
        DooingleCount(owner = userE, count = 11),
        DooingleCount(owner = userF, count = 7)
    )

}