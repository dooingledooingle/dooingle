package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.oauth2.provider.OAuth2Provider
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialUserRepository : CrudRepository<SocialUser, Long>, SocialUserQueryDslRepository {
    fun existsByProviderAndProviderId(provider: OAuth2Provider, providerId: String): Boolean
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): SocialUser
    fun existsByUserLink(userLink: String): Boolean
}
