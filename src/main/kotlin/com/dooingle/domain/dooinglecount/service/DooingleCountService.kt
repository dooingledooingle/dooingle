package com.dooingle.domain.dooinglecount.service

import com.dooingle.domain.dooinglecount.repository.DooingleCountRedisRepository
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DooingleCountService(
    private val dooingleCountRedisRepository: DooingleCountRedisRepository
) {

    fun plusCount(owner: SocialUser) {
        dooingleCountRedisRepository.plusCount(owner.id.toString(), owner.nickname)
        // TODO : userLink로 바꾼다면 owner.id -> owner.userLink로 바꿔야 함
    }

    fun getHotDooinglerList(size: Long): List<DooinglerResponse> {
        return dooingleCountRedisRepository.getHighCountDooinglers(size)?.map {
            DooinglerResponse(
                userId = it.substringBefore(":").toLong(),
                nickname = it.substringAfter(":")
            )
        } ?: emptyList()
    }

    // 매일 0시 0분 0초에 redis 모든 데이터 삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun deleteAllDooingleCount() {
        dooingleCountRedisRepository.deleteAll()
    }

}