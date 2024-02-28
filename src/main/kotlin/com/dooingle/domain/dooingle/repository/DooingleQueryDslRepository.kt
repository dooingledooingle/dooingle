package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooinglerResponse

interface DooingleQueryDslRepository {
    fun getHotDooinglerList(): List<DooinglerResponse>
}