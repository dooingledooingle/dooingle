package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.user.dto.DooinglerResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class DooingleCountRedisRepository(
    redisTemplate: RedisTemplate<String, String>,
) {

    val zSetOperations = redisTemplate.opsForZSet()

    fun plusCount(userId: String, nickname: String) {
        val member = "$userId:$nickname"
        zSetOperations.addIfAbsent(KEY, member, 0.0)
        zSetOperations.incrementScore(KEY, member, 1.0)
    }

    fun getHighCountDooinglers(size: Long): List<DooinglerResponse>? {
        val rank: Set<String>? = zSetOperations.reverseRange(KEY, 0, size-1)

        return rank?.map {
            DooinglerResponse(
                userId = it.substringBefore(":").toLong(),
                nickname = it.substringAfter(":")
            )
        }
    }

    fun deleteAll() {
        TODO("Not yet implemented")
    }

    companion object {
        const val KEY = "dooingleCount"
    }

}