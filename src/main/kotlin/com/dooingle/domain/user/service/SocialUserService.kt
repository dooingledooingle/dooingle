package com.dooingle.domain.user.service

import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.domain.user.dto.OAuth2UserInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SocialUserService(
    private val socialUserRepository: SocialUserRepository,
    private val profileRepository: ProfileRepository,
    private val dooingleCountService: DooingleCountService
) {

    @Transactional
    fun registerIfAbsent(oAuth2UserInfo: OAuth2UserInfo): SocialUser {
        return if (!socialUserRepository.existsByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)) {
            val socialUser = SocialUser(
                provider = oAuth2UserInfo.provider,
                providerId = oAuth2UserInfo.id,
                nickname = oAuth2UserInfo.nickname
            )

            oAuth2UserInfo.profileImage?.let {
                profileRepository.save(
                    Profile(user = socialUser, profileImage = oAuth2UserInfo.profileImage))
            }

            socialUserRepository.save(socialUser)
        } else {
            socialUserRepository.findByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)
        }
    }

    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            "new" -> socialUserRepository.getNewDooinglers()
            else -> throw IllegalArgumentException() // TODO
        }
    }

}
