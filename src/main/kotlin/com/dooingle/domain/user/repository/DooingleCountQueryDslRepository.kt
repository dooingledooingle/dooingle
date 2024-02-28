package com.dooingle.domain.user.repository

import com.dooingle.domain.user.dto.DooinglerResponse

interface DooingleCountQueryDslRepository {
    fun getHighCountDooinglers(): List<DooinglerResponse>
}