package com.dooingle.domain.user.service

import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val dooingleCountService: DooingleCountService
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            else -> TODO("새 뒹글러 목록 조회")
        }
    }

}
