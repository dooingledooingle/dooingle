package com.dooingle.global.aop

import com.dooingle.domain.badreport.dto.AddBadReportRequest
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.badreport.repository.BadReportRepository
import com.dooingle.domain.badreport.service.BadReportService
import com.dooingle.domain.catchdomain.dto.AddCatchRequest
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.catchdomain.service.CatchService
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.follow.service.FollowService
import com.dooingle.domain.notification.repository.NotificationRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class DistributedLockTest @Autowired constructor(
    // private val dooingleService: DooingleService,
    private val followService: FollowService,
    private val catchService: CatchService,
    private val badReportService: BadReportService,
    private val dooingleRepository: DooingleRepository,
    private val notificationRepository: NotificationRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountRepository: DooingleCountRepository,
    private val followRepository: FollowRepository,
    private val badReportRepository: BadReportRepository,
)  {

    // @MockkBean private lateinit var notificationService: NotificationService
    private val THREAD_COUNT = 2
    // private val BIG_THREAD_COUNT = 100

    @AfterEach
    fun clearData() {
        badReportRepository.deleteAll()
        notificationRepository.deleteAll()
        catchRepository.deleteAll()
        dooingleRepository.deleteAll()
        dooingleCountRepository.deleteAll()
        followRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

    /*
    TODO DooingleService에서 DistributedLock을 사용하지 않게 됐기 때문에 이 테스트는 주석 처리함
     - 하루 동안 특정 페이지의 뒹글 개수를 세는 기능 자체를 Redis를 이용하게 되면서 DooingleCount 테이블을 사용할 필요가 없게 됨
     - DooingleService의 addDooingle()에서 DistributedLock의 목적은 DooingleCount와 관련된 경합을 다루는 목적이 컸음
       - DooingleCount에서 같은 owner_id를 같은 레코드를 제어해야했기 때문임
     - 혹시 addDooingle()에서 또 DistributedLock을 필요로 하는 부분이 있는지 점검 후 적용 가능하다면 적용할 것
    @Test
    fun `특정 유저에게 동시에 많은 뒹글 등록 요청이 들어왔을때 뒹글의 개수와 Count값이 일치해야 한다`(){
        // GIVEN
        val executor = Executors.newFixedThreadPool(BIG_THREAD_COUNT)
        val barrier = CyclicBarrier(BIG_THREAD_COUNT)
        val guestIdList = mutableListOf<Long>()

        repeat(BIG_THREAD_COUNT) {
            socialUserRepository.save(SocialUser(nickname = "guest", provider = OAuth2Provider.KAKAO, providerId = "aaaa", userLink = "aaaa"))
                .let { guestIdList.add(it.id!!) }
        }
        val owner = socialUserRepository.save(SocialUser(nickname = "owner", provider = OAuth2Provider.KAKAO, providerId = "bbbb", userLink = "bbbb"))
        //dooingleCountRepository.save(DooingleCount(owner = owner))

        every { notificationService.addDooingleNotification(any(), any()) } just runs

        // WHEN
        for (guestId in guestIdList) {
            executor.execute {
                barrier.await()
                dooingleService.addDooingle(
                    fromUserId = guestId,
                    ownerUserLink = owner.userLink,
                    addDooingleRequest = AddDooingleRequest("뒹글")
                )
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)

        // THEN
        dooingleCountRepository.findByOwnerId(ownerId = owner.id!!)!!.count shouldBe BIG_THREAD_COUNT
    }
     */

    @Test
    fun `동일한 팔로우 요청이 여러번 들어오면 한번만 처리되어야 한다`(){
        // GIVEN
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)
        val barrier = CyclicBarrier(THREAD_COUNT)

        val fromUser = socialUserRepository.save(SocialUser(nickname = "fromUser", provider = OAuth2Provider.KAKAO, providerId = "aaaa", userLink = "aaaa"))
        val toUser = socialUserRepository.save(SocialUser(nickname = "toUser", provider = OAuth2Provider.KAKAO, providerId = "bbbb", userLink = "bbbb"))

        // WHEN
        repeat(THREAD_COUNT) {
            executor.execute {
                barrier.await()
                followService.follow(
                    toUserLink = toUser.userLink,
                    fromUserId = fromUser.id!!
                )
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)

        // THEN
        followRepository.findAllByFromUser(fromUser).size shouldBe 1
    }

    @Test
    fun `뒹글 주인이 캐치 등록을 여러번 요청해도 캐치는 하나만 등록되어야 한다`(){
        // GIVEN
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)
        val barrier = CyclicBarrier(THREAD_COUNT)

        val guest = socialUserRepository.save(SocialUser(nickname = "guest", provider = OAuth2Provider.KAKAO, providerId = "aaaa", userLink = "aaaa"))
        val owner = socialUserRepository.save(SocialUser(nickname = "owner", provider = OAuth2Provider.KAKAO, providerId = "bbbb", userLink = "bbbb"))
        val dooingle = dooingleRepository.save(Dooingle(guest = guest, owner = owner, content = "뒹글", catch = null))

        // WHEN
        repeat(THREAD_COUNT) {
            executor.execute {
                barrier.await()
                catchService.addCatch(
                    dooingleId = dooingle.id!!,
                    ownerId = owner.id!!,
                    addCatchRequest = AddCatchRequest("캐치${it+1}")
                )
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)

        // THEN
        catchRepository.findAll().size shouldBe 1
    }

    @Test
    fun `동일한 신고 요청이 여러번 들어오면 한번만 처리되어야 한다`(){
        // GIVEN
        val executor = Executors.newFixedThreadPool(THREAD_COUNT)
        val barrier = CyclicBarrier(THREAD_COUNT)

        val guest = socialUserRepository.save(SocialUser(nickname = "guest", provider = OAuth2Provider.KAKAO, providerId = "aaaa", userLink = "aaaa"))
        val owner = socialUserRepository.save(SocialUser(nickname = "owner", provider = OAuth2Provider.KAKAO, providerId = "bbbb", userLink = "bbbb"))
        val dooingle = dooingleRepository.save(Dooingle(guest = guest, owner = owner, content = "뒹글", catch = null))

        // WHEN
        repeat(THREAD_COUNT) {
            executor.execute {
                barrier.await()
                badReportService.addReport(
                    reporterId = owner.id!!,
                    addBadReportRequest = AddBadReportRequest(
                        reportedTargetType = ReportedTargetType.DOOINGLE,
                        reportedTargetId = dooingle.id!!,
                        reportReason = "도배"
                    )
                )
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)

        // THEN
        badReportRepository.findAll().size shouldBe 1
    }
}
