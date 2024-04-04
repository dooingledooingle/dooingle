package com.dooingle.domain.catchdomain.service

import com.dooingle.domain.catchdomain.dto.AddCatchRequest
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.exception.custom.ConflictStateException
import com.dooingle.global.exception.custom.NotPermittedException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import com.dooingle.global.redis.RedisConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
class CatchServiceDBTest @Autowired constructor(
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val distributedLock: DistributedLock,
)
{
    private val mockNotificationService = mockk<NotificationService>(relaxed = true)
    private val catchService = CatchService(dooingleRepository,catchRepository,mockNotificationService, distributedLock)

    @AfterEach
    fun clearData() {
        catchRepository.deleteAll()
        dooingleRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    @Test
    fun `캐치가 정상적으로 등록될 경우`(){
        // given
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val owner = userA
        val dooingle = dooingle
        val addCatchRequest = AddCatchRequest("캐치 테스트")

        every { mockNotificationService.addCatchNotification(any(), any()) } just runs

        // when
        catchService.addCatch(dooingle.id!!, owner.id!!, addCatchRequest)

        // then
        catchRepository.count() shouldBe 1
        dooingle.catch shouldNotBe null
        dooingle.catch!!.content shouldBe addCatchRequest.content
    }

    @Test
    fun `뒹글의 오너가 아닌 사람이 캐치를 등록하려 할 경우 예외 발생`(){
        // given
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val guest = userB
        val dooingle = dooingle
        val addCatchRequest = AddCatchRequest("캐치 테스트")

        // expected
        shouldThrow<NotPermittedException> { catchService.addCatch(dooingle.id!!, guest.id!!, addCatchRequest) }
    }

    @Test
    fun `정상적으로 캐치가 삭제될 경우 딜리티드엣 업데이트`(){
        // given
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val owner = userA
        val dooingle = dooingle
        val addCatchRequest = AddCatchRequest("캐치 테스트")

        every { mockNotificationService.addCatchNotification(any(), any()) } just runs

        // when
        val catchResponse = catchService.addCatch(dooingle.id!!, owner.id!!, addCatchRequest)
        catchService.deleteCatch(dooingle.id!!, catchResponse.catchId!!, owner.id!!)

        // then
        catchRepository.findById(catchResponse.catchId!!).get().deletedAt shouldNotBe null
    }

    @Test
    fun `이미 이전에 캐치를 단 이력이 있는 경우 예외 발생`(){
        // given
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val owner = userA
        val dooingle = dooingle
        val addCatchRequest1 = AddCatchRequest("캐치 테스트")
        val addCatchRequest2 = AddCatchRequest("추가적인 캐치")

        // expected
        catchService.addCatch(dooingle.id!!, owner.id!!, addCatchRequest1)
        shouldThrow<ConflictStateException> { catchService.addCatch(dooingle.id!!, owner.id!!, addCatchRequest2) }
    }

    @Test
    fun `뒹글의 오너가 아닌 사람이 삭제를 시도할 경우 예외 발생`(){
        // given
        socialUserRepository.saveAll(userList)
        dooingleRepository.saveAll(dooingleList)

        val owner = userA
        val guest = userB
        val dooingle = dooingle
        val addCatchRequest = AddCatchRequest("캐치 테스트")

        // expected
        val catchResponse = catchService.addCatch(dooingle.id!!, owner.id!!, addCatchRequest)
        shouldThrow<NotPermittedException> { catchService.deleteCatch(dooingle.id!!, catchResponse.catchId!!, guest.id!!) }
    }

    private val userA = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "1111111111")
    private val userB = SocialUser(nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "2222222222")
    private val userC = SocialUser(nickname = "C", provider = OAuth2Provider.KAKAO, providerId = "3", userLink = "3333333333")
    private val userList = listOf(userA, userB, userC)

    private val dooingle = Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null)
    private val dooingleList = listOf(dooingle)

//    private val dooingleList = listOf(
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null),
//        Dooingle(owner = userA, guest = userB, content = "A에게 질문", catch = null),
//        Dooingle(owner = userB, guest = userC, content = "B에게 질문", catch = null),
//        Dooingle(owner = userC, guest = userD, content = "C에게 질문", catch = null),
//        Dooingle(owner = userD, guest = userA, content = "D에게 질문", catch = null)
//    )

}