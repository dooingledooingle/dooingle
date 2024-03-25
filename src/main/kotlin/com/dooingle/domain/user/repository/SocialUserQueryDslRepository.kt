package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse

interface SocialUserQueryDslRepository {
    fun getNewDooinglers(size: Long): List<DooinglerResponse>
}