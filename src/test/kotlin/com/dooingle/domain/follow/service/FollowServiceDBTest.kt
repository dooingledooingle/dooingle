package com.dooingle.domain.follow.service

import com.dooingle.domain.follow.dto.FollowDetailResponse
import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.exception.custom.ConflictStateException
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import com.dooingle.global.redis.RedisConfig
import io.kotest.assertions.throwables.shouldThrow
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
@Import(value = [QueryDslConfig::class, RedisConfig::class, DistributedLock::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class FollowServiceDBTest (
    private val socialUserRepository: SocialUserRepository,
    private val followRepository: FollowRepository,
    private val distributedLock: DistributedLock
){
    private val followService = FollowService(followRepository, socialUserRepository, distributedLock)

    @AfterEach
    fun clearData() {
        followRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `팔로우를 정상 등록한 경우`(){

        // given
        socialUserRepository.saveAll(userList)

        val fromUser = userA
        val toUser = userB
        val toUserLink = userB.userLink

        // when
        followService.follow(toUser.userLink, fromUser.id!!)

        // then
        followRepository.count() shouldBe 1
        followService.showFollowersNumber(toUserLink) shouldBe 1
        followRepository.findAllByFromUser(userA).first().toUser.id!! shouldBe userB.id!!
    }

    @Test
    fun `자기 자신을 팔로우한 경우 예외 발생`(){

        // given
        socialUserRepository.saveAll(userList)

        val fromUser = userA
        val toUser = userA

        // expected
        shouldThrow<InvalidParameterException> { followService.follow(toUser.userLink, fromUser.id!!) }
    }

    @Test
    fun `이미 팔로우한 사람을 팔로우할 경우 예외 발생`(){

        // given
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)

        val fromUser = userA
        val toUser = userB

        // expected
        shouldThrow<ConflictStateException> {followService.follow(toUser.userLink, fromUser.id!!)}
    }

    @Test
    fun `정상적으로 팔로우 삭제가 될 경우`(){

        // given
        socialUserRepository.saveAll(userList)
        followRepository.saveAll(followingList)

        val fromUser = userA
        val toUser = userB
        val toUserLink = userB.userLink

        // when
        followService.cancelFollowing(toUser.userLink,fromUser.id!!)

        // then
        followRepository.findAllByFromUser(userA).count() shouldBe 1
        followService.showFollowersNumber(toUserLink) shouldBe 0
        followRepository.findAllByFromUser(userA).map { it.toUser }.contains(userB) shouldBe false

    }

    @Test
    fun `팔로우가 이미 되어있지 않은 상태에서 팔로우를 삭제시도 할 경우 예외 발생`(){

        // given
        socialUserRepository.saveAll(userList)

        val fromUser = userA
        val toUser = userB

        // expected
        shouldThrow<ConflictStateException>{followService.cancelFollowing(toUser.userLink,fromUser.id!!)}

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

}