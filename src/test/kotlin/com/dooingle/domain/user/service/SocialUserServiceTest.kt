package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectResult
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.UpdateProfileDto
import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import java.net.URL

@DisplayName("SocialUserService 단위 테스트")
class SocialUserServiceTest : AnnotationSpec() {

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

    @BeforeEach
    fun setup() {
        profile1 = createProfile(user = socialUser1, description = "defaultDescription", imageUrl = "https://default.com")
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
        result1.nickname shouldBe "testNickname_1"
        result1.description shouldBe "defaultDescription"
        result1.imageUrl shouldBe "https://default.com"

        result2.nickname shouldBe "testNickname_2"
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
    fun `프로필 수정 시 소개글을 수정하고자 하면 소개글만 변경되어야 한다`(){
        //프로필 사진이 기존에 있을때 -> Url이 그대로 나와야함, 없는 경우 -> 그대로 null로
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns socialUser1
        every { mockSocialUserRepository.findByIdOrNull(socialUser2.id!!) } returns socialUser2
        every { mockProfileRepository.findByUser(socialUser1) } returns profile1
        every { mockProfileRepository.findByUser(socialUser2) } returns null
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser1 }) } returns newProfile1
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser2 }) } returns newProfile2

        val requestDto1 = UpdateProfileDto(description = "updatedDescription", imageUrl = profile1.imageUrl)
        val requestDto2 = UpdateProfileDto(description = "updatedDescription", imageUrl = null)

        // when
        val result1 = socialUserService.updateProfile(socialUser1.id!!, requestDto1, null)
        val result2 = socialUserService.updateProfile(socialUser2.id!!, requestDto2, null)

        // then
        result1.description shouldBe "updatedDescription"
        result1.imageUrl shouldBe "https://default.com"

        result2.description shouldBe "updatedDescription"
        result2.imageUrl shouldBe null
    }

    @Test
    fun `프로필 수정 시 프로필 사진을 수정하고자 하면 프로필 사진만 변경되어야 한다`(){
        //프로필 사진이 기존에 있는 경우, 없는 경우 둘다 테스트
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns socialUser1
        every { mockSocialUserRepository.findByIdOrNull(socialUser2.id!!) } returns socialUser2
        every { mockProfileRepository.findByUser(socialUser1) } returns profile1
        every { mockProfileRepository.findByUser(socialUser2) } returns null
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser1 }) } returns newProfile3
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser2 }) } returns newProfile4
        every { mockAmazonS3.putObject(any(), any(), any(), any()) } returns PutObjectResult()
        every { mockAmazonS3.getUrl(any(), any()) } returns URL("https://updated.com")

        val requestDto1 = UpdateProfileDto(description = profile1.description, imageUrl = profile1.imageUrl)
        val requestDto2 = UpdateProfileDto(description = null, imageUrl = null)

        // when
        val result1 = socialUserService.updateProfile(socialUser1.id!!, requestDto1, iamgeFile)
        val result2 = socialUserService.updateProfile(socialUser2.id!!, requestDto2, iamgeFile)

        // then
        result1.description shouldBe "defaultDescription"
        result1.imageUrl shouldBe "https://updated.com"

        result2.description shouldBe null
        result2.imageUrl shouldBe "https://updated.com"
    }

    @Test
    fun `프로필 수정 시 프로필 사진을 수정하고자 할 때 지원하지 않는 확장자인 경우 예외가 발생되어야 한다`(){
        // given
        val requestDto = UpdateProfileDto(description = profile1.description, imageUrl = profile1.imageUrl)

        // when
        val result = kotlin.runCatching { socialUserService.updateProfile(socialUser1.id!!, requestDto, invalidIamgeFile) }

        // then
        result.shouldNotBeSuccess()
        shouldThrow<InvalidParameterException> { result.getOrThrow() }
    }

    @Test
    fun `프로필 수정 시 프로필 사진과 소개글을 모두 수정하고자 하면 이미지url과 소개글이 모두 변경되어야 한다`(){
        //프로필 사진이 기존에 있는 경우, 없는 경우 둘다 테스트
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns socialUser1
        every { mockSocialUserRepository.findByIdOrNull(socialUser2.id!!) } returns socialUser2
        every { mockProfileRepository.findByUser(socialUser1) } returns profile1
        every { mockProfileRepository.findByUser(socialUser2) } returns null
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser1 }) } returns newProfile5
        every { mockProfileRepository.save(match { profile -> profile.user == socialUser2 }) } returns newProfile6
        every { mockAmazonS3.putObject(any(), any(), any(), any()) } returns PutObjectResult()
        every { mockAmazonS3.getUrl(any(), any()) } returns URL("https://updated.com")

        val requestDto1 = UpdateProfileDto(description = "updatedDescription", imageUrl = profile1.imageUrl)
        val requestDto2 = UpdateProfileDto(description = "updatedDescription", imageUrl = null)

        // when
        val result1 = socialUserService.updateProfile(socialUser1.id!!, requestDto1, iamgeFile)
        val result2 = socialUserService.updateProfile(socialUser2.id!!, requestDto2, iamgeFile)

        // then
        result1.description shouldBe "updatedDescription"
        result1.imageUrl shouldBe "https://updated.com"

        result2.description shouldBe "updatedDescription"
        result2.imageUrl shouldBe "https://updated.com"
    }

    @Test
    fun `프로필 수정 시 userid에 해당하는 회원이 없는 경우 예외가 발생되어야 한다`(){
        // given
        every { mockSocialUserRepository.findByIdOrNull(socialUser1.id!!) } returns null

        val requestDto = UpdateProfileDto(description = null, imageUrl = null)

        // when
        val result = kotlin.runCatching { socialUserService.updateProfile(socialUser1.id!!, requestDto, null) }

        // then
        result.shouldNotBeSuccess()
        shouldThrow<ModelNotFoundException> { result.getOrThrow() }
    }

    companion object {
        private fun createSocialUser(id:Long, provider: OAuth2Provider, providerId:String, nickname:String) = SocialUser(
            id = id,
            provider = provider,
            providerId = providerId,
            nickname = nickname
        )

        private fun createProfile(user:SocialUser, description: String?, imageUrl:String?) = Profile(
            user = user,
            description = description,
            imageUrl = imageUrl
        )

        private val socialUser1 = createSocialUser(id = 1L, provider = OAuth2Provider.KAKAO, providerId = "testProviderId_1", nickname = "testNickname_1")
        private val socialUser2 = createSocialUser(id = 2L, provider = OAuth2Provider.KAKAO, providerId = "testProviderId_2", nickname = "testNickname_2")
        private var profile1 = createProfile(user = socialUser1, description = "defaultDescription", imageUrl = "https://default.com")

        private val iamgeFile = MockMultipartFile(
            "testFile", "testFile.jpg", "image/jpg", "test".toByteArray()
        )
        private val invalidIamgeFile = MockMultipartFile(
            "testFile", "testFile.gif", "image/gif", "test".toByteArray()
        )

        //소개글만 변경
        private val newProfile1 = createProfile(user = socialUser1, description = "updatedDescription", imageUrl = "https://default.com")
        private val newProfile2 = createProfile(user = socialUser2, description = "updatedDescription", imageUrl = null)

        //이미지만 변경
        private val newProfile3 = createProfile(user = socialUser1, description = "defaultDescription", imageUrl = "https://updated.com")
        private val newProfile4 = createProfile(user = socialUser2, description = null, imageUrl = "https://updated.com")

        //모두 변경 (소개글, 이미지)
        private val newProfile5 = createProfile(user = socialUser1, description = "updatedDescription", imageUrl = "https://updated.com")
        private val newProfile6 = createProfile(user = socialUser2, description = "updatedDescription", imageUrl = "https://updated.com")
    }
}
