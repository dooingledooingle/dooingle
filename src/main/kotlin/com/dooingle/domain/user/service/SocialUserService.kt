package com.dooingle.domain.user.service

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.OAuth2UserInfo
import org.springframework.stereotype.Service

@Service
class SocialUserService(
    private val socialUserRepository: SocialUserRepository
) {

    fun registerIfAbsent(oAuth2UserInfo: OAuth2UserInfo): SocialUser {
        return if (!socialUserRepository.existsByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)) {
            SocialUser(
                provider = oAuth2UserInfo.provider,
                providerId = oAuth2UserInfo.id,
                nickname = oAuth2UserInfo.nickname,
                profileImage = oAuth2UserInfo.profileImage
            ).let { socialUserRepository.save(it) }
        } else {
            socialUserRepository.findByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)
        }
    }

}
