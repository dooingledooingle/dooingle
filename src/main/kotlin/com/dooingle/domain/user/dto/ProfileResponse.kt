package com.dooingle.domain.user.dto

data class ProfileResponse(
    val nickname:String,
    val description:String?,
    val imageUrl:String?
)
