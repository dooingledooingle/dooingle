package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.catch.repository.CatchRepository
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
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
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

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
    
    @Test
    fun `뒹글을 등록할 때 존재하지 않는 guestId를 전달하면 예외가 발생한다`() {
        // given
        val owner = getFixtureOfOwner()
        val guest = getFixtureOfGuest()
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
        val owner = getFixtureOfOwner()
        val guest = getFixtureOfGuest()
        val dooingleAdditionRequest = AddDooingleRequest(guest.id!!, "새 뒹글 내용")

        every { mockSocialUserRepository.findByIdOrNull(guest.id!!) } returns guest
        every { mockSocialUserRepository.findByIdOrNull(owner.id!!) } returns null // 존재하지 않는 ownerId 가정

        // when
        val result = kotlin.runCatching { dooingleService.addDooingle(owner.id!!, dooingleAdditionRequest) }

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
