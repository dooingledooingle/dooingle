package com.dooingle.domain.user.service

import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val dooingleCountService: DooingleCountService
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            "new" -> userRepository.getNewDooinglers()
            else -> throw IllegalArgumentException() // TODO
        }
    }

}
