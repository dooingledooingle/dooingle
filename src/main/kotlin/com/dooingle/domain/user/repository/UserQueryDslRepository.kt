package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse

interface UserQueryDslRepository {
    fun getNewDooinglers(): List<DooinglerResponse>
}