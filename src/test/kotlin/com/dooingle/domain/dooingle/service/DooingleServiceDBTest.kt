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
import com.dooingle.global.property.DooinglersProperties
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
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

    @MockBean
    lateinit var dooinglersProperties: DooinglersProperties

    private val mockNotificationService = mockk<NotificationService>()

    private val dooingleService = DooingleService(
        dooingleRepository,
        socialUserRepository,
        catchRepository,
        dooingleCountRepository,
        mockNotificationService
    )

    @BeforeEach
    fun clearData() {
        dooingleRepository.deleteAll()
        followRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `팔로우 피드 조회 시 커서가 전달되지 않는다면 팔로우하는 유저에게 굴러온 뒹글 목록을 최신 글부터 조회한다`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)
        dooingleRepository.saveAll(dooingleList)

        val userId: Long = userA.id!!

        // WHEN
        val result = dooingleService.getDooingleFeedOfFollowing(userId, null, DEFAULT_PAGE_REQUEST)

        // THEN
        result.content.forEach { it.ownerId shouldBeIn listOf(userB.id, userC.id) }

        val dooinglesFollowing = dooingleRepository.findAll()
            .filter { it.owner.id == userB.id || it.owner.id == userC.id }
            .sortedByDescending { it.id }
        result.content[0].dooingleId shouldBe dooinglesFollowing[0].id

        for (i in 0 until result.content.size - 1) {
            result.content[i].dooingleId shouldBeGreaterThan result.content[i + 1].dooingleId
        }

        result.content.size shouldBe DooingleFeedController.PAGE_SIZE
        result.hasNext() shouldBe true
    }

    @Test
    fun `팔로우 피드 조회 시 커서가 전달되면 팔로우하는 유저에게 굴러온 뒹글 목록을 커서 이전 글부터 조회한다`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)
        dooingleRepository.saveAll(dooingleList)

        val userId: Long = userA.id!!
        val cursor: Long = 6

        // WHEN
        val result = dooingleService.getDooingleFeedOfFollowing(userId, cursor, DEFAULT_PAGE_REQUEST)

        result.content.forEach { it.ownerId shouldBeIn listOf(userB.id, userC.id) }

        val dooinglesFollowingBeforeCursor = dooingleRepository.findAll()
            .filter { it.owner.id == userB.id || it.owner.id == userC.id }
            .filter { it.id!! < cursor }
            .sortedByDescending { it.id }
        result.content[0].dooingleId shouldBe dooinglesFollowingBeforeCursor[0].id

        for (i in 0 until result.content.size - 1) {
            result.content[i].dooingleId shouldBeGreaterThan result.content[i + 1].dooingleId
        }

        result.content.size shouldBe dooinglesFollowingBeforeCursor.size
        result.hasNext() shouldBe false
    }

    @Test
    fun `팔로우 피드 조회 시 전달된 user id의 유저가 존재하지 않는다면 예외가 발생한다`() {
        // GIVEN
        socialUserRepository.saveAll(userList)
        val userId: Long = 100

        // WHEN & THEN
        shouldThrow<ModelNotFoundException> {
            dooingleService.getDooingleFeedOfFollowing(userId, null, DEFAULT_PAGE_REQUEST)
        }

        socialUserRepository.findByIdOrNull(userId) shouldBe null
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2")
    private val userC = SocialUser(nickname = "C", provider = OAuth2Provider.KAKAO, providerId = "3")
    private val userD = SocialUser(nickname = "D", provider = OAuth2Provider.KAKAO, providerId = "4")
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
