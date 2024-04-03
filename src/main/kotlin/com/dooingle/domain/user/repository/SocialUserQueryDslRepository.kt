package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.SearchDooinglerResponse

interface SocialUserQueryDslRepository {
    fun getNewDooinglers(size: Long): List<DooinglerResponse>

    fun searchDooinglers(nickname: String): List<SearchDooinglerResponse>

    fun getDooingler(userId: Long): DooinglerResponse
}