package com.dooingle.domain.dooinglecount.service

import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRedisRepository
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DooingleCountService(
    private val dooingleCountRedisRepository: DooingleCountRedisRepository,
    private val dooingleRepository: DooingleRepository
) {

    fun plusCount(owner: SocialUser) {
        dooingleCountRedisRepository.plusCount(owner.userLink, owner.nickname)
    }

    fun getHotDooinglerList(size: Long): List<DooinglerResponse> {
        return runCatching {
            dooingleCountRedisRepository.getHighCountDooinglers(size)?.map {
                DooinglerResponse(
                    userLink = it.substringBefore(":"),
                    nickname = it.substringAfter(":")
                )
            } ?: dooingleRepository.getHotDooinglerList(size)
        }.getOrElse {
            dooingleRepository.getHotDooinglerList(size)
        }
    }

    // 매일 0시 0분 0초에 redis 모든 데이터 삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun deleteAllDooingleCount() {
        dooingleCountRedisRepository.deleteAll()
    }

}