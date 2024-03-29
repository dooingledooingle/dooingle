package com.dooingle.domain.follow.dto

data class FollowDetailResponse(
    val followingUserName: String,
    val followingUserLink: String,
    val followingUserProfileImageUrl: String,
    val followingUserDescription: String,
)
