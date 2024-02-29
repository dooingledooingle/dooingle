package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.user.dto.DooinglerResponse

interface DooingleCountQueryDslRepository {
    fun getHighCountDooinglers(): List<DooinglerResponse>
}