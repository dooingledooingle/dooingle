package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.OAuth2UserInfo
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.property.DooinglersProperties
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class SocialUserServiceDBTest(
    private val socialUserRepository: SocialUserRepository,
    private val profileRepository: ProfileRepository,
) {
    @MockBean lateinit var dooinglersProperties: DooinglersProperties

    private val dooingleCountService: DooingleCountService = mockk<DooingleCountService>()
    private val amazonS3 = mockk<AmazonS3>()
    private val bucketName = "mockName"
    private val socialUserService = SocialUserService(socialUserRepository, profileRepository, dooingleCountService, amazonS3, bucketName)

    @BeforeEach
    fun clearData() {
        socialUserRepository.deleteAll()
        profileRepository.deleteAll()
    }

    @Test
    fun `회원 등록 시 프로필 이미지는 존재할 때만 저장`() {
        // GIVEN
        val oauth2UserInfo1 = OAuth2UserInfo(
            provider = OAuth2Provider.KAKAO, id = "1",
            nickname = "A",
            profileImage = null
        )
        val oauth2UserInfo2 = OAuth2UserInfo(
            provider = OAuth2Provider.KAKAO, id = "2",
            nickname = "B",
            profileImage = "imgUrl"
        )

        // WHEN
        val result1 = socialUserService.registerUser(oauth2UserInfo1)
        val result2 = socialUserService.registerUser(oauth2UserInfo2)

        // THEN
        val user1 = socialUserRepository.findByProviderAndProviderId(oauth2UserInfo1.provider, oauth2UserInfo1.id)
        result1.id shouldBe user1.id
        result1.provider shouldBe oauth2UserInfo1.provider
        result1.providerId shouldBe oauth2UserInfo1.id
        result1.nickname shouldBe oauth2UserInfo1.nickname

        val user2 = socialUserRepository.findByProviderAndProviderId(oauth2UserInfo2.provider, oauth2UserInfo2.id)
        result2.id shouldBe user2.id
        result2.provider shouldBe oauth2UserInfo2.provider
        result2.providerId shouldBe oauth2UserInfo2.id
        result2.nickname shouldBe oauth2UserInfo2.nickname

        val profile1 = profileRepository.findByUser(result1)
        profile1 shouldBe null

        val profile2 = profileRepository.findByUser(result2)
        profile2 shouldNotBe null
        profile2!!.imageUrl shouldBe oauth2UserInfo2.profileImage
    }

    @Test
    fun `OAuth2 사용자 정보가 기존 회원에 없으면 등록, 있으면 조회`() {
        // GIVEN
        val oauth2UserInfo1 = OAuth2UserInfo(
            provider = OAuth2Provider.KAKAO, id = "1",
            nickname = "A",
            profileImage = "imgUrl"
        )
        socialUserRepository.existsByProviderAndProviderId(oauth2UserInfo1.provider, oauth2UserInfo1.id) shouldBe false

        val oauth2UserInfo2 = OAuth2UserInfo(
            provider = OAuth2Provider.KAKAO, id = "2",
            nickname = "B",
            profileImage = "imgUrl"
        )
        val user2 = socialUserService.registerUser(oauth2UserInfo2)

        // WHEN
        val result1 = socialUserService.registerIfAbsent(oauth2UserInfo1)
        val result2 = socialUserService.registerIfAbsent(oauth2UserInfo2)

        // THEN
        val user1 = socialUserRepository.findByProviderAndProviderId(oauth2UserInfo1.provider, oauth2UserInfo1.id)
        result1.id shouldBe user1.id
        result1.provider shouldBe oauth2UserInfo1.provider
        result1.providerId shouldBe oauth2UserInfo1.id
        result1.nickname shouldBe oauth2UserInfo1.nickname

        val profile1 = profileRepository.findByUser(result1)
        profile1 shouldNotBe null
        profile1!!.imageUrl shouldBe oauth2UserInfo1.profileImage

        result2.id shouldBe user2.id
        result2.provider shouldBe oauth2UserInfo2.provider
        result2.providerId shouldBe oauth2UserInfo2.id
        result2.nickname shouldBe oauth2UserInfo2.nickname

        val profile2 = profileRepository.findByUser(result2)
        profile2 shouldNotBe null
        profile2!!.imageUrl shouldBe oauth2UserInfo2.profileImage
    }

}