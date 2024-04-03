package com.dooingle.global.aop

import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooingle.service.DooingleService
import com.dooingle.domain.dooinglecount.model.DooingleCount
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import com.dooingle.global.redis.RedisConfig
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class, RedisConfig::class, DistributedLock::class])
@ActiveProfiles("test")
class DistributedLockTest @Autowired constructor(
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountRepository: DooingleCountRepository,
    private val followRepository: FollowRepository,
    private val distributedLock: DistributedLock,
)  {

    private val THREAD_COUNT = 100
    private val mockNotificationService = mockk<NotificationService>(relaxed = true)

    private val dooingleService = DooingleService(
        dooingleRepository,
        socialUserRepository,
        catchRepository,
        dooingleCountRepository,
        mockNotificationService,
        distributedLock
    )

    @AfterEach
    fun clearData() {
        catchRepository.deleteAll()
        dooingleRepository.deleteAll()
        dooingleCountRepository.deleteAll()
        followRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `100명의 유저가 1명의 유저에게 동시에 뒹글을 등록하는 경우 뒹글 Count는 100이어야 한다`(){
        // GIVEN
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)
        val barrier = CyclicBarrier(THREAD_COUNT)

        repeat(THREAD_COUNT) {
            socialUserRepository.save(SocialUser(nickname = "guest", provider = OAuth2Provider.KAKAO, providerId = "aaaa", userLink = "aaaa"))
        }
        val owner = socialUserRepository.save(SocialUser(nickname = "owner", provider = OAuth2Provider.KAKAO, providerId = "bbbb", userLink = "bbbb"))

        every { mockNotificationService.addDooingleNotification(any(), any()) } just runs
        dooingleCountRepository.save(DooingleCount(owner = owner))

        // WHEN
        repeat(THREAD_COUNT) {
            executor.execute {
                barrier.await()
                dooingleService.addDooingle(
                    fromUserId = it.toLong()+1L,
                    ownerUserLink = owner.userLink,
                    addDooingleRequest = AddDooingleRequest("뒹글")
                )
            }
        }
        executor.awaitTermination(30, TimeUnit.SECONDS)

        // THEN
        val ownerDooingleCount = dooingleCountRepository.findByOwnerId(ownerId = owner.id!!)
        ownerDooingleCount!!.count shouldBe THREAD_COUNT
    }
}
