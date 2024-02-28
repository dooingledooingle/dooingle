package com.dooingle.domain.user.service

import com.dooingle.domain.user.repository.DooingleCountRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DooingleCountService(
    private val dooingleCountRepository: DooingleCountRepository
) {

    // 매일 0시 0분 0초에 dooingle_count 모든 데이터 삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun deleteAllDooingleCount() {
        dooingleCountRepository.deleteAllInBatch()
    }

}