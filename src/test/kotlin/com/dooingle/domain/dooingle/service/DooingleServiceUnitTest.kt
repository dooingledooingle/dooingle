package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catch.dto.CatchResponse
import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.catch.repository.CatchRepository
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldNotBeSuccess
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

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
    lateinit var dooingleAndCatchResponseSlice: Slice<DooingleAndCatchResponse>

    @BeforeAll
    fun prepareFixture() {
        owner = getFixtureOfOwner()
        guest = getFixtureOfGuest()
        // dooingleAndCatchResponseSlice = SliceImpl(getFixtureOfDooingleAndCatchResponseList())
    }

    @AfterEach
    fun clearMockingLogics() {
        clearAllMocks()
    }

    // TODO - addDooingleRequest.to(guest, owner)에서 반환하는 dooingle에 임의로 id를 넣을 수 없어서
    //   return DooingleResponse.from(dooingle) 부분에서 NPE가 발생할 수밖에 없음
//     @Test fun `뒹글을 등록하는 데에 성공하면, 등록된 뒹글을 확인할 수 있다`() {
//        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns owner
//        every { mockDooingleRepository.save(any()) } returns Dooingle(
//            guest = guest,
//            owner = owner,
//            catch = null,
//            content = "새로 등록 뒹글 내용",
//        ).also { it.id = (getFixtureOfDooingleList().size + 1).toLong() }
//        every { mockDooingleCountRepository.findByOwnerId(owner.id!!) } returns dooingleCount
//        every { dooingleCount.plus() } just Runs
//     }
    
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

    // TODO DooingleService의 getPage() 메서드 중 DooingleResponse들로 구성된 Slice를 가져온 후에 그 DooingleResponse 안의 catch를 매핑하는 부분 존재함
    //   확장 함수로 된 이 부분을 mocking 할 수 없어서 단위 테스트 코드 보류
//    @Test
//    fun `개인 뒹글 페이지 최신 뒹글+캐치 스크롤을 조회할 때 존재하는 페이지 소유자 id(ownerId)를 전달하면 해당 페이지의 최신 뒹글+캐치를 확인할 수 있다`() {
//        // given
//        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns owner
//        every { mockDooingleRepository.getPersonalPageBySlice(owner, null, any()) } returns dooingleAndCatchResponseSlice
//
//        // when
//        val result = kotlin.runCatching { dooingleService.getPage(owner.id!!, guest.id!!, null) }
//
//        // then
//        result.shouldNotBeSuccess()
//        shouldThrow<Exception> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `개인 뒹글 페이지 최신 바로 다음의 뒹글+캐치 스크롤을 조회할 때 존재하는 페이지 소유자 id(ownerId)를 전달하면 해당 페이지의 최신 바로 다음의 뒹글+캐치를 확인할 수 있다`() {
//    }

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
