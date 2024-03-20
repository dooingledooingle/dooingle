package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

@DisplayName("SocialUserService 단위 테스트")
class SocialUserServiceTest : AnnotationSpec(){

    private val mockSocialUserRepository = mockk<SocialUserRepository>()
    private val mockProfileRepository = mockk<ProfileRepository>()
    private val mockDooingleCountService = mockk<DooingleCountService>()
    private val mockAmazonS3 = mockk<AmazonS3>()
    private val socialUserService = SocialUserService(
        socialUserRepository = mockSocialUserRepository,
        profileRepository = mockProfileRepository,
        dooingleCountService = mockDooingleCountService,
        amazonS3 = mockAmazonS3,
        bucketName = "testBucketName"
    )

    lateinit var socialUser1: SocialUser
    lateinit var socialUser2: SocialUser
    lateinit var profile1: Profile

    @BeforeAll
    fun prepareFixture() {
        socialUser1 = SocialUser(
            id = 1L,
            provider = OAuth2Provider.KAKAO,
            providerId = "testProviderId1",
            nickname = "testNickname1"
        )
        socialUser2 = SocialUser(
            id = 2L,
            provider = OAuth2Provider.KAKAO,
            providerId = "testProviderId2",
            nickname = "testNickname2"
        ) //socialUser2는 프로필이 생성되지 않은 회원
        profile1 = Profile(
            user = socialUser1,
            description = "testDescription",
            imageUrl = "testImageUrl"
        )
    }

    @AfterEach
    fun clearMockingLogics() {
        clearAllMocks()
    }

    @Test
    fun `프로필 조회 시 이름은 반드시 반환되어야 한다`(){
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns socialUser1
        every { mockSocialUserRepository.findByIdOrNull(socialUser2.id!!) } returns socialUser2
        every { mockProfileRepository.findByUser(socialUser1) } returns profile1
        every { mockProfileRepository.findByUser(socialUser2) } returns null

        // when
        val result1 = socialUserService.getProfile(socialUser1.id!!)
        val result2 = socialUserService.getProfile(socialUser2.id!!)

        // then
        result1.nickname shouldBe "testNickname1"
        result1.description shouldBe "testDescription"
        result1.imageUrl shouldBe "testImageUrl"

        result2.nickname shouldBe "testNickname2"
        result2.description shouldBe null
        result2.imageUrl shouldBe null
    }

    @Test
    fun `프로필 조회 시 userid에 해당하는 회원이 없는 경우 예외가 발생되어야 한다`(){
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns null

        // when
        val result = kotlin.runCatching { socialUserService.getProfile(socialUser1.id!!) }

        // then
        result.shouldNotBeSuccess()
        shouldThrow<ModelNotFoundException> { result.getOrThrow() }
    }

    @Test
    fun `회원이 소개글을 수정하고자 하면 소개글만 변경되어야 한다`(){
        //프로필 사진이 기존에 있는 경우, 없는 경우 둘다 테스트
        // given
        // when
        // then
    }

    @Test
    fun `회원이 프로필 사진을 수정하고자 하면 프로필 사진만 변경되어야 한다`(){
        //프로필 사진이 기존에 있는 경우, 없는 경우 둘다 테스트
        // given
        // when
        // then
    }

    @Test
    fun `프로필 사진과 소개글을 모두 수정하고자 하면 이미지url과 소개글이 모두 변경되어야 한다`(){
        //프로필 사진이 기존에 있는 경우, 없는 경우 둘다 테스트
        // given
        // when
        // then
    }

    @Test
    fun `프로필 수정 시 userid에 해당하는 회원이 없는 경우 예외가 발생되어야 한다`(){
        // given
        // when
        // then
    }
}
