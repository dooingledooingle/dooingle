package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.controller.DooingleFeedController
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class DooingleServiceDBTest(
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountRepository: DooingleCountRepository,
    private val followRepository: FollowRepository
) {

    private val mockNotificationService = mockk<NotificationService>()

    private val dooingleService = DooingleService(
        dooingleRepository,
        socialUserRepository,
        catchRepository,
        dooingleCountRepository,
        mockNotificationService
    )

    @AfterEach
    fun clearData() {
        dooingleRepository.deleteAll()
        followRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `팔로우하는 유저 페이지의 최신 뒹글부터 조회`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)
        dooingleRepository.saveAll(dooingleList)

        val userId: Long = userA.id!!

        // WHEN
        val result = dooingleService.getDooingleFeedOfFollowing(userId, null, DEFAULT_PAGE_REQUEST)

        // THEN
        val followingUserLinkList = followRepository.findAllByFromUser(userA).map { it.toUser.userLink }
        result.content.forEach { it.ownerUserLink shouldBeIn followingUserLinkList }

        val dooinglesFollowingSorted = dooingleRepository.findAll()
            .filter { followingUserLinkList.contains(it.owner.userLink) }
            .sortedByDescending { it.id }
        result.zip(dooinglesFollowingSorted) { response, entity -> response.dooingleId shouldBe entity.id }

        result.content.size shouldBe DooingleFeedController.PAGE_SIZE
        result.hasNext() shouldBe true
    }

    @Test
    fun `커서와 함께 팔로우 피드 조회 시 커서 이전 글부터 조회`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)
        val dooingles = dooingleRepository.saveAll(dooingleList)

        val userId: Long = userA.id!!
        val cursor: Long = dooingles[22].id!!

        // WHEN
        val result = dooingleService.getDooingleFeedOfFollowing(userId, cursor, DEFAULT_PAGE_REQUEST)

        // THEN
        val followingUserLinkList = followRepository.findAllByFromUser(userA).map { it.toUser.userLink }
        result.content.forEach { it.ownerUserLink shouldBeIn followingUserLinkList }

        val dooinglesFollowingSorted = dooingleRepository.findAll()
            .filter { followingUserLinkList.contains(it.owner.userLink) && it.id!! < cursor }
            .sortedByDescending { it.id }
        result.zip(dooinglesFollowingSorted) { response, entity -> response.dooingleId shouldBe entity.id }

        result.content.size shouldBe dooinglesFollowingSorted.size
        result.hasNext() shouldBe false
    }

    @Test
    fun `다른 유저 팔로우하지 않는 경우 팔로우 피드 조회 시 0건의 결과`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val userId: Long = userA.id!!

        // WHEN
        val result = dooingleService.getDooingleFeedOfFollowing(userId, null, DEFAULT_PAGE_REQUEST)

        // THEN
        result.content.size shouldBe 0
        result.hasNext() shouldBe false
    }

    @Test
    fun `존재하지 않는 유저의 팔로우 피드 조회 시 예외 발생`() {
        // GIVEN
        val userId: Long = 1000000

        // WHEN & THEN
        socialUserRepository.findByIdOrNull(userId) shouldBe null

        shouldThrow<ModelNotFoundException> {
            dooingleService.getDooingleFeedOfFollowing(userId, null, DEFAULT_PAGE_REQUEST)
        }
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "1111111111")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "2222222222")
    private val userC = SocialUser(nickname = "C", provider = OAuth2Provider.KAKAO, providerId = "3", userLink = "3333333333")
    private val userD = SocialUser(nickname = "D", provider = OAuth2Provider.KAKAO, providerId = "4", userLink = "4444444444")
    private val userList = listOf(userA, userB, userC, userD)

    private val followingList = listOf(
        Follow(fromUser = userA, toUser = userB),
        Follow(fromUser = userA, toUser = userC)
    )

    private val dooingleList = listOf(
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
    )

    private val DEFAULT_PAGE_REQUEST = PageRequest.ofSize(DooingleFeedController.PAGE_SIZE)

}
