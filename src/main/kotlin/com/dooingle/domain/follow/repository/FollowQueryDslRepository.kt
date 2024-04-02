package com.dooingle.domain.follow.repository

import com.dooingle.domain.follow.dto.FollowDetailResponse
import com.dooingle.domain.user.model.SocialUser

interface FollowQueryDslRepository {

    fun getFollowDetailListByFromUser(fromUser: SocialUser): List<FollowDetailResponse>
}
