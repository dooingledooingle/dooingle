package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catch.repository.CatchRepository
import com.dooingle.domain.dooingle.controller.DooingleFeedController
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
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
    private val dooingleService = DooingleService(
        dooingleRepository = mockDooingleRepository,
        socialUserRepository = mockSocialUserRepository,
        catchRepository = mockCatchRepository,
        dooingleCountRepository = mockDooingleCountRepository,
    )

    lateinit var owner: SocialUser
    lateinit var guest: SocialUser
//    lateinit var dooingleAndCatchResponseSlice: Slice<DooingleAndCatchResponse>
    lateinit var dooingleResponseList: List<DooingleResponse>
    lateinit var theLatestSliceOfDooingleResponseList: Slice<DooingleResponse>
    lateinit var theNextOfLatestSliceOfDooingleResponseList: Slice<DooingleResponse>

    @BeforeAll
    fun prepareFixture() {
        owner = getFixtureOfOwner()
        guest = getFixtureOfGuest()
        // dooingleAndCatchResponseSlice = SliceImpl(getFixtureOfDooingleAndCatchResponseList())
        dooingleResponseList = getFixtureOfDooingleResponseList()
        theLatestSliceOfDooingleResponseList = SliceImpl(dooingleResponseList.subList(0, DooingleFeedController.PAGE_SIZE))
        theNextOfLatestSliceOfDooingleResponseList = SliceImpl(
            dooingleResponseList.subList(
                DooingleFeedController.PAGE_SIZE,
                min(DooingleFeedController.PAGE_SIZE + DooingleFeedController.PAGE_SIZE, dooingleResponseList.size),
            )
        )
    }

    @AfterEach
    fun clearMockingLogics() {
        clearAllMocks()
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
    fun `뒹글을 등록할 때 존재하지 않는 guestId를 전달하면 예외가 발생한다`() {
        // given
        val dooingleAdditionRequest = AddDooingleRequest(guest.id!!, "새 뒹글 내용")

        every { mockSocialUserRepository.findByIdOrNull(guest.id!!) } returns null // 존재하지 않는 guestId 가정

        // when
        val result = kotlin.runCatching { dooingleService.addDooingle(owner.id!!, dooingleAdditionRequest) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
        result.shouldNotBeSuccess()
        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `뒹글을 등록할 때 존재하지 않는 ownerId를 전달하면 예외가 발생한다`() {
        // given
        val dooingleAdditionRequest = AddDooingleRequest(guest.id!!, "새 뒹글 내용")

        every { mockSocialUserRepository.findByIdOrNull(guest.id!!) } returns guest
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
        val result = kotlin.runCatching { dooingleService.addDooingle(owner.id!!, dooingleAdditionRequest) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
        result.shouldNotBeSuccess()
        shouldThrow<Exception> { result.getOrThrow() }
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
    fun `개인 뒹글 페이지 최신 뒹글+캐치 스크롤을 조회할 때 존재하지 않는 페이지 소유자 id(ownerId)를 전달하면 예외가 발생한다`() {
        // given
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, null) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
        result.shouldNotBeSuccess()
        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `개인 뒹글 페이지 최신 바로 다음의 뒹글+캐치 스크롤을 조회할 때 존재하지 않는 페이지 소유자 id(ownerId)를 전달하면 예외가 발생한다`() {
        // given
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, DooingleService.USER_FEED_PAGE_SIZE.toLong()) }

        // then // TODO 예외 처리 공통화 이후 더 자세하게 예외 점검할 것
        result.shouldNotBeSuccess()
        shouldThrow<Exception> { result.getOrThrow() }
    }

    @Test
    fun `최신 뒹글 피드를 조회하고자 하면 최신 뒹글 피드를 조회할 수 있다`() {
        // given
        val cursor: Long? = null // 최신 뒹글 피드 조건
        val pageRequest = PageRequest.ofSize(DooingleFeedController.PAGE_SIZE)
        every { mockDooingleRepository.getDooinglesBySlice(cursor, pageRequest) } returns theLatestSliceOfDooingleResponseList

        // when
        val result = dooingleService.getDooingleFeeds(cursor, PageRequest.ofSize(DooingleFeedController.PAGE_SIZE))

        // then
        /*result.size shouldBe DooingleFeedController.PAGE_SIZE // 실제 가져오는 크기가 PAGE_SIZE보다 작은 경우 문제*/
        result.content.first().dooingleId shouldBe theLatestSliceOfDooingleResponseList.first().dooingleId
    }

    @Test
    fun `최신 뒹글 피드의 다음 스크롤을 조회하고자 하면 최신 뒹글 피드의 다음 스크롤을 조회할 수 있다`() {
        // given
        val cursor = DooingleFeedController.PAGE_SIZE.toLong() // 최신 뒹글 피드 조건
        val pageRequest = PageRequest.ofSize(DooingleFeedController.PAGE_SIZE)
        every { mockDooingleRepository.getDooinglesBySlice(cursor, pageRequest) } returns theNextOfLatestSliceOfDooingleResponseList

        // when
        val result = dooingleService.getDooingleFeeds(cursor, PageRequest.ofSize(DooingleFeedController.PAGE_SIZE))

        // then
        /*result.size shouldBe DooingleFeedController.PAGE_SIZE // 실제 가져오는 크기가 PAGE_SIZE보다 작은 경우 문제*/
        result.content.first().dooingleId shouldBe theNextOfLatestSliceOfDooingleResponseList.first().dooingleId
    }

    private fun getFixtureOfOwner() = SocialUser(
        id = ownerId,
        provider = ownerOAuthProvider,
        providerId = ownerIdFromOAuthProvider,
        nickname = ownerNickname,
    )

    private fun getFixtureOfGuest() = SocialUser(
        id = guestId,
        provider = guestOAuthProvider,
        providerId = guestIdFromOAuthProvider,
        nickname = guestNickname,
    )

//    private fun getFixtureOfDooingleAndCatchResponseList() = listOf<DooingleAndCatchResponse>(
//        // TODO - DooingleAndCatchResponse에서 Catch 엔티티를 담지 않도록 하는 편이 좋을 것 같다고 생각함
//        DooingleAndCatchResponse(owner.nickname, 1, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 2, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 3, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 4, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 5, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 6, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 7, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 8, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 9, "뒹글 내용", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 10, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 11, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 12, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 13, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 14, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//        DooingleAndCatchResponse(owner.nickname, 15, "뒹글 내용 1", Catch("캐치 내용", Dooingle(guest, owner, null, "뒹글 내용"),null), ZonedDateTime.now()),
//    ).sortedBy { it.dooingleId }

    private fun getFixtureOfDooingleResponseList() = listOf<DooingleResponse>(
        DooingleResponse(owner.nickname, 1, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 2, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 3, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 4, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 5, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 6, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 7, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 8, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 9, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 10, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 11, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 12, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 13, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 14, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 15, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 16, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 17, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 18, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 19, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 20, "뒹글 내용", ZonedDateTime.now()),
        DooingleResponse(owner.nickname, 21, "뒹글 내용", ZonedDateTime.now()),
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
