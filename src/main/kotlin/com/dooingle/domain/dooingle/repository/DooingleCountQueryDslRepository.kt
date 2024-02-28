package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooinglerResponse

interface DooingleCountQueryDslRepository {
    fun getHighCountDooinglers(): List<DooinglerResponse>
}