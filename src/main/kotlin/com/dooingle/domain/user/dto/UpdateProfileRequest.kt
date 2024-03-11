package com.dooingle.domain.user.dto

import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(max = 100)
    val description:String?
)
