package com.dooingle.domain.user.service

import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.repository.DooingleCountRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val dooingleCountRepository: DooingleCountRepository
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountRepository.getHighCountDooinglers()
            else -> TODO("새 뒹글러 목록 조회")
        }
    }

}
