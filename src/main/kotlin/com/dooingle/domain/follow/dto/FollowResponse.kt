package com.dooingle.domain.follow.dto

import com.dooingle.domain.follow.model.Follow

data class FollowResponse(
    val followingUserName: String
) {
    companion object {
        fun from(follow: Follow): FollowResponse {
            return FollowResponse(
                followingUserName = follow.toUser.nickname
            )
        }
    }
}
