package com.dooingle.domain.follow.repository

import com.dooingle.domain.follow.dto.FollowDetailResponse
import com.dooingle.domain.follow.model.QFollow
import com.dooingle.domain.user.model.QProfile
import com.dooingle.domain.user.model.QSocialUser
import com.dooingle.domain.user.model.SocialUser
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class FollowQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FollowQueryDslRepository {

    private val follow = QFollow.follow
    private val user = QSocialUser.socialUser
    private val profile = QProfile.profile

    override fun getFollowDetailListByFromUser(fromUser: SocialUser): List<FollowDetailResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    FollowDetailResponse::class.java,
                    user.nickname,
                    user.userLink,
                    profile.imageUrl,
                    profile.description
                )
            )
            .from(follow)
            .join(user).on(follow.toUser.eq(user))
            .leftJoin(profile).on(follow.toUser.eq(profile.user))
            .where(follow.fromUser.eq(fromUser))
            .fetch()
    }
}
