package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.controller.DooingleFeedController
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleFeedResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.model.QDooingle.dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.mockito.ArgumentMatchers.any
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime
import kotlin.math.min

@DisplayName("DooingleService 단위 테스트")
class DooingleServiceUnitTest : AnnotationSpec() {

    private val mockDooingleRepository = mockk<DooingleRepository>()
    private val mockSocialUserRepository = mockk<SocialUserRepository>()
    private val mockCatchRepository = mockk<CatchRepository>()
    private val mockDooingleCountRepository = mockk<DooingleCountRepository>()
    private val mockNotificationService = mockk<NotificationService>()
    private val mockDistributedLock = mockk<DistributedLock>()
    private val dooingleService = DooingleService(
        dooingleRepository = mockDooingleRepository,
        socialUserRepository = mockSocialUserRepository,
        catchRepository = mockCatchRepository,
        dooingleCountRepository = mockDooingleCountRepository,
        notificationService = mockNotificationService,
        distributedLock = mockDistributedLock
    )

    lateinit var owner: SocialUser
    lateinit var guest: SocialUser
//    lateinit var dooingleAndCatchResponseSlice: Slice<DooingleAndCatchResponse>
    lateinit var dooingleFeedResponseList: List<DooingleFeedResponse>
    lateinit var theLatestSliceOfDooingleFeedResponseList: Slice<DooingleFeedResponse>
    lateinit var theNextOfLatestSliceOfDooingleResponseList: Slice<DooingleFeedResponse>

    @BeforeAll
    fun prepareFixture() {
        owner = getFixtureOfOwner()
        guest = getFixtureOfGuest()
        // dooingleAndCatchResponseSlice = SliceImpl(getFixtureOfDooingleAndCatchResponseList())
        dooingleFeedResponseList = getFixtureOfDooingleResponseList()
        theLatestSliceOfDooingleFeedResponseList = SliceImpl(dooingleFeedResponseList.subList(0, DooingleFeedController.PAGE_SIZE))
        theNextOfLatestSliceOfDooingleResponseList = SliceImpl(
            dooingleFeedResponseList.subList(
                DooingleFeedController.PAGE_SIZE,
                min(DooingleFeedController.PAGE_SIZE + DooingleFeedController.PAGE_SIZE, dooingleFeedResponseList.size),
            )
        )
    }

    @AfterEach
    fun clearMockingLogics() {
        clearAllMocks()
    }

    @Test
    fun `뒹글 등록시 오너아이디와 게스트아이디가 같을 경우 예외 발생`(){
        // given
        val ownerUserLink = "1111111111"
        val fromUserId = 1L
        val addDooingleRequest = AddDooingleRequest("일해라뇌!!")

        every { mockSocialUserRepository.findByUserLink(ownerUserLink) } returns owner
        every { mockSocialUserRepository.findByIdOrNull(fromUserId) } returns owner

        // expected
        shouldThrow<InvalidParameterException> { dooingleService.addDooingle(fromUserId, ownerUserLink, addDooingleRequest) }
    }


    @Test
    fun `커서값이 전달 되지 않았을 경우 가장 최신 뒹글부터 개인 페이지 조회`() {
        // given
        val ownerUserLink = "1111111111"
        val cursor = null
        val pageRequest = PageRequest.ofSize(10)

        every { mockSocialUserRepository.findByUserLink(ownerUserLink) } returns owner
        every { mockDooingleRepository.getPersonalPageBySlice(owner, cursor, pageRequest) } returns SliceImpl(getFixtureOfDooingleAndCatchResponseList().subList(0, 10))

        // when
        val slice = dooingleService.getPage(ownerUserLink, cursor)

        // then
        slice.content.size shouldBe 10
        slice.content.first().dooingleId shouldBe getFixtureOfDooingleAndCatchResponseList().first().dooingleId
        slice.isFirst shouldBe true
    }

    @Test
    fun `커서값이 전달 되었을 경우 커서 이전 뒹글 부터 개인 페이지 조회`(){
        // given
        val ownerUserLink = "1111111111"
        val cursor = 22L
        val pageRequest = PageRequest.ofSize(10)

        every { mockSocialUserRepository.findByUserLink(ownerUserLink) } returns owner
        every { mockDooingleRepository.getPersonalPageBySlice(owner, cursor, pageRequest) } returns SliceImpl(getFixtureOfDooingleAndCatchResponseList().subList(22, 32))

        // when
        val slice = dooingleService.getPage(ownerUserLink, cursor)

        // then
        slice.content.size shouldBe 10
        slice.content.first().dooingleId shouldBe cursor
        slice.content.last().dooingleId shouldBe (cursor - 10) + 1
        slice.isFirst shouldBe true
    }


    /*
     TODO - addDooingleRequest.to(guest, owner)에서 반환하는 dooingle에 임의로 id를 넣을 수 없어서
       return DooingleResponse.from(dooingle) 부분에서 NPE가 발생할 수밖에 없음
     @Test fun `뒹글을 등록하는 데에 성공하면, 등록된 뒹글을 확인할 수 있다`() {
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns owner
        every { mockDooingleRepository.save(any()) } returns Dooingle(
            guest = guest,
            owner = owner,
            catch = null,
            content = "새로 등록 뒹글 내용",
        ).also { it.id = (getFixtureOfDooingleList().size + 1).toLong() }
        every { mockDooingleCountRepository.findByOwnerId(owner.id!!) } returns dooingleCount
        every { dooingleCount.plus() } just Runs
     }
     */
    
    @Test
    fun `존재하지 않는 guestId를 전달하면 뒹글을 등록할 때 예외가 발생한다`() {
        // given
//        val dooingleAdditionRequest = AddDooingleRequest(guest.id!!, "새 뒹글 내용")

        every { mockSocialUserRepository.findByIdOrNull(guest.id!!) } returns null // 존재하지 않는 guestId 가정

        // when
//        val result = kotlin.runCatching { dooingleService.addDooingle(owner.id!!, dooingleAdditionRequest) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
//        result.shouldNotBeSuccess()
//        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `존재하지 않는 ownerId를 전달하면 뒹글을 등록할 때 예외가 발생한다`() {
        // given
//        val dooingleAdditionRequest = AddDooingleRequest(guest.id!!, "새 뒹글 내용")

        every { mockSocialUserRepository.findByIdOrNull(guest.id!!) } returns guest
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
//        val result = kotlin.runCatching { dooingleService.addDooingle(owner.id!!, dooingleAdditionRequest) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
//        result.shouldNotBeSuccess()
//        shouldThrow<Exception> { result.getOrThrow() }
    }

    /*
     TODO DooingleService의 getPage() 메서드 중 DooingleResponse들로 구성된 Slice를 가져온 후에 그 DooingleResponse 안의 catch를 매핑하는 부분 존재함
       확장 함수로 된 이 부분을 mocking 할 수 없어서 단위 테스트 코드 보류
    @Test
    fun `개인 뒹글 페이지 최신 뒹글+캐치 스크롤을 조회할 때 존재하는 페이지 소유자 id(ownerId)를 전달하면 해당 페이지의 최신 뒹글+캐치를 확인할 수 있다`() {
        // given
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns owner
        every { mockDooingleRepository.getPersonalPageBySlice(owner, null, any()) } returns dooingleAndCatchResponseSlice

        // when
        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, null) }

        // then
        result.shouldNotBeSuccess()
        shouldThrow<Exception> { result.getOrThrow() }
    }
    @Test
    fun `개인 뒹글 페이지 최신 바로 다음의 뒹글+캐치 스크롤을 조회할 때 존재하는 페이지 소유자 id(ownerId)를 전달하면 해당 페이지의 최신 바로 다음의 뒹글+캐치를 확인할 수 있다`() {
    }
     */

    @Test
    fun `존재하지 않는 페이지 소유자 id(ownerId)를 전달하면 개인 뒹글 페이지 최신 뒹글+캐치 스크롤을 조회할 때 예외가 발생한다`() {
        // given
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
//        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, null) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
//        result.shouldNotBeSuccess()
//        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `존재하지 않는 페이지 소유자 id(ownerId)를 전달하면 개인 뒹글 페이지 최신 바로 다음의 뒹글+캐치 스크롤을 조회할 때 예외가 발생한다`() {
        // given
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
//        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, DooingleService.USER_FEED_PAGE_SIZE.toLong()) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
//        result.shouldNotBeSuccess()
//        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `최신 뒹글 피드를 조회하고자 하면 최신 뒹글 피드를 조회할 수 있다`() {
        // given
        val cursor: Long? = null // 최신 뒹글 피드 조건
        val pageRequest = PageRequest.ofSize(DooingleFeedController.PAGE_SIZE)
        every { mockDooingleRepository.getDooinglesBySlice(cursor, pageRequest) } returns theLatestSliceOfDooingleFeedResponseList

        // when
        val result = dooingleService.getDooingleFeed(cursor, PageRequest.ofSize(DooingleFeedController.PAGE_SIZE))

        // then
        /*result.size shouldBe DooingleFeedController.PAGE_SIZE // 실제 가져오는 크기가 PAGE_SIZE보다 작은 경우 문제*/
        result.content.first().dooingleId shouldBe theLatestSliceOfDooingleFeedResponseList.first().dooingleId
    }

    @Test
    fun `최신 뒹글 피드의 다음 스크롤을 조회하고자 하면 최신 뒹글 피드의 다음 스크롤을 조회할 수 있다`() {
        // given
        val cursor = DooingleFeedController.PAGE_SIZE.toLong() // 최신 뒹글 피드 다음 스크롤 조건
        val pageRequest = PageRequest.ofSize(DooingleFeedController.PAGE_SIZE)
        every { mockDooingleRepository.getDooinglesBySlice(cursor, pageRequest) } returns theNextOfLatestSliceOfDooingleResponseList

        // when
        val result = dooingleService.getDooingleFeed(cursor, PageRequest.ofSize(DooingleFeedController.PAGE_SIZE))

        // then
        /*result.size shouldBe DooingleFeedController.PAGE_SIZE // 실제 가져오는 크기가 PAGE_SIZE보다 작은 경우 문제*/
        result.content.first().dooingleId shouldBe theNextOfLatestSliceOfDooingleResponseList.first().dooingleId
    }

    private fun getFixtureOfOwner() = SocialUser(
        id = ownerId,
        provider = ownerOAuthProvider,
        providerId = ownerIdFromOAuthProvider,
        nickname = ownerNickname,
        userLink = "0000000000",
    )

    private fun getFixtureOfGuest() = SocialUser(
        id = guestId,
        provider = guestOAuthProvider,
        providerId = guestIdFromOAuthProvider,
        nickname = guestNickname,
        userLink = "1111111111",
    )

    private fun getFixtureOfDooingleAndCatchResponseList() = listOf<DooingleAndCatchResponse>(
        // TODO - DooingleAndCatchResponse에서 Catch 엔티티를 담지 않도록 하는 편이 좋을 것 같다고 생각함
        DooingleAndCatchResponse(owner.nickname, "1111111111", 1, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(1)),
        DooingleAndCatchResponse(owner.nickname, "2222222222", 2, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(2)),
        DooingleAndCatchResponse(owner.nickname, "3333333333", 3, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(3)),
        DooingleAndCatchResponse(owner.nickname, "4444444444", 4, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(4)),
        DooingleAndCatchResponse(owner.nickname, "5555555555", 5, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(5)),
        DooingleAndCatchResponse(owner.nickname, "6666666666", 6, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(6)),
        DooingleAndCatchResponse(owner.nickname, "7777777777", 7, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(7)),
        DooingleAndCatchResponse(owner.nickname, "8888888888", 8, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(8)),
        DooingleAndCatchResponse(owner.nickname, "9999999999", 9, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(9)),
        DooingleAndCatchResponse(owner.nickname, "0000000000", 10, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(10)),
        DooingleAndCatchResponse(owner.nickname, "aaaaaaaaaa", 11, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(11)),
        DooingleAndCatchResponse(owner.nickname, "bbbbbbbbbb", 12, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(12)),
        DooingleAndCatchResponse(owner.nickname, "cccccccccc", 13, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(13)),
        DooingleAndCatchResponse(owner.nickname, "dddddddddd", 14, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(14)),
        DooingleAndCatchResponse(owner.nickname, "eeeeeeeeee", 15, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(15)),
        DooingleAndCatchResponse(owner.nickname, "ffffffffff", 16, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(16)),
        DooingleAndCatchResponse(owner.nickname, "gggggggggg", 17, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(17)),
        DooingleAndCatchResponse(owner.nickname, "hhhhhhhhhh", 18, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(18)),
        DooingleAndCatchResponse(owner.nickname, "iiiiiiiiii", 19, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(19)),
        DooingleAndCatchResponse(owner.nickname, "jjjjjjjjjj", 20, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(20)),
        DooingleAndCatchResponse(owner.nickname, "kkkkkkkkkk", 21, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(21)),
        DooingleAndCatchResponse(owner.nickname, "llllllllll", 22, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(22)),
        DooingleAndCatchResponse(owner.nickname, "1111111111", 23, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(23)),
        DooingleAndCatchResponse(owner.nickname, "2222222222", 24, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(24)),
        DooingleAndCatchResponse(owner.nickname, "3333333333", 25, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(25)),
        DooingleAndCatchResponse(owner.nickname, "4444444444", 26, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(26)),
        DooingleAndCatchResponse(owner.nickname, "5555555555", 27, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(27)),
        DooingleAndCatchResponse(owner.nickname, "6666666666", 28, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(28)),
        DooingleAndCatchResponse(owner.nickname, "7777777777", 29, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(29)),
        DooingleAndCatchResponse(owner.nickname, "8888888888", 30, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(30)),
        DooingleAndCatchResponse(owner.nickname, "9999999999", 31, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(31)),
        DooingleAndCatchResponse(owner.nickname, "0000000000", 32, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(32)),
        DooingleAndCatchResponse(owner.nickname, "aaaaaaaaaa", 33, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(33)),
        DooingleAndCatchResponse(owner.nickname, "bbbbbbbbbb", 34, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(34)),
        DooingleAndCatchResponse(owner.nickname, "cccccccccc", 35, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(35)),
        DooingleAndCatchResponse(owner.nickname, "dddddddddd", 36, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(36)),
        DooingleAndCatchResponse(owner.nickname, "eeeeeeeeee", 37, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(37)),
        DooingleAndCatchResponse(owner.nickname, "ffffffffff", 38, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(38)),
        DooingleAndCatchResponse(owner.nickname, "gggggggggg", 39, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(39)),
        DooingleAndCatchResponse(owner.nickname, "hhhhhhhhhh", 40, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(40)),
        DooingleAndCatchResponse(owner.nickname, "iiiiiiiiii", 41, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(41)),
        DooingleAndCatchResponse(owner.nickname, "jjjjjjjjjj", 42, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(42)),
        DooingleAndCatchResponse(owner.nickname, "kkkkkkkkkk", 43, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(43)),
        DooingleAndCatchResponse(owner.nickname, "llllllllll", 44, "뒹글 내용", null, ZonedDateTime.now().plusSeconds(44)),
    ).sortedByDescending { it.dooingleId }

    private fun getFixtureOfDooingleResponseList(): List<DooingleFeedResponse> = listOf<DooingleFeedResponse>(
        DooingleFeedResponse(owner.nickname, "1111111111", 1, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 2, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 3, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 4, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 5, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 6, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 7, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 8, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 9, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 10, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 11, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 12, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 13, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 14, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 15, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 16, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 17, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 18, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 19, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 20, "뒹글 내용", false, ZonedDateTime.now()),
        DooingleFeedResponse(owner.nickname, "1111111111", 21, "뒹글 내용", false, ZonedDateTime.now()),
    ).sortedBy { it.dooingleId }

    companion object {
        private const val ownerId = 1.toLong()
        private val ownerOAuthProvider = OAuth2Provider.KAKAO
        private const val ownerIdFromOAuthProvider = "1"
        private const val ownerNickname = "뒹글 페이지 주인"

        private const val guestId = 2.toLong()
        private val guestOAuthProvider = OAuth2Provider.KAKAO
        private const val guestIdFromOAuthProvider = "2"
        private const val guestNickname = "뒹글 작성자"
    }
}
