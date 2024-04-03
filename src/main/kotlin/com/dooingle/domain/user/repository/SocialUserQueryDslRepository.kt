package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.DooinglerWithProfileResponse

interface SocialUserQueryDslRepository {
    fun getNewDooinglers(size: Long): List<DooinglerResponse>

    fun searchDooinglers(nickname: String): List<DooinglerWithProfileResponse>

    fun getRandomDooinglers(size: Long): List<DooinglerWithProfileResponse>

    fun getDooingler(userId: Long): DooinglerResponse
}