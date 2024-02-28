package com.dooingle.domain.dooingle.service

import com.dooingle.domain.dooingle.dto.DooinglerResponse
import com.dooingle.domain.dooingle.repository.DooingleRepository
import org.springframework.stereotype.Service

@Service
class DooingleService(
    private val dooingleRepository: DooingleRepository
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleRepository.getHotDooinglerList()
            else -> TODO("새 뒹글러 목록 조회")
        }
    }

}
