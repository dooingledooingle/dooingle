package com.dooingle.domain.dooingle.service

import com.dooingle.domain.dooingle.dto.DooinglerResponse
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.global.aop.StopWatch
import org.springframework.stereotype.Service

@Service
class DooingleService(
    private val dooingleRepository: DooingleRepository
) {
    @StopWatch
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleRepository.getHotDooinglerList()
            else -> TODO("새 뒹글러 목록 조회")
        }
    }

}
