package com.dooingle.domain.user.dto

data class SearchDooinglerResponse(
    val nickname: String,
    val userLink: String,
    val imageUrl: String?,
    val description: String?
)