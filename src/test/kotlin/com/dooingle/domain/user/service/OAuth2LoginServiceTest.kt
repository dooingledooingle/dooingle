package com.dooingle.domain.user.service

import com.dooingle.domain.user.dto.OAuth2UserInfo
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.model.UserRole
import com.dooingle.global.jwt.JwtHelper
import com.dooingle.global.oauth2.client.OAuth2ClientService
import com.dooingle.global.oauth2.provider.OAuth2Provider
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class OAuth2LoginServiceTest {

    private val oAuthClientService: OAuth2ClientService = mockk<OAuth2ClientService>()
    private val socialUserService: SocialUserService = mockk<SocialUserService>()
    private val jwtHelper: JwtHelper = mockk<JwtHelper>()

    private val oAuth2LoginService = OAuth2LoginService(oAuthClientService, socialUserService, jwtHelper)

    @Test
    fun `OAuth2 로그인 성공 시 액세스 토큰 반환`() {
        // GIVEN
        val provider = OAuth2Provider.KAKAO
        val authorizationCode = "code"
        val oAuth2UserInfo = OAuth2UserInfo(
            provider = provider, id = "1",
            nickname = "A",
            profileImage = null
        )
        every { oAuthClientService.getUserInfo(provider, authorizationCode) } returns oAuth2UserInfo

        val user = mockk<SocialUser>()
        val userId: Long = 1
        val userRole = UserRole.USER
        every { socialUserService.registerIfAbsent(oAuth2UserInfo) } returns user
        every { user.id } returns userId
        every { user.role } returns userRole

        val accessToken = "token"
        every { jwtHelper.generateAccessToken(userId, userRole.toString()) } returns accessToken

        // WHEN
        val result = oAuth2LoginService.login(provider, authorizationCode)

        // THEN
        result shouldBe accessToken
    }

}