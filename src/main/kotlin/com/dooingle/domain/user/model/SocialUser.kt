package com.dooingle.domain.user.model

import com.dooingle.global.entity.BaseEntity
import com.dooingle.global.oauth2.provider.OAuth2Provider
import jakarta.persistence.*

@Entity
@Table(name = "social_user")
class SocialUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    val provider: OAuth2Provider,
    val providerId: String,
    val nickname: String,
    val profileImage: String?,

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
) : BaseEntity() {
}