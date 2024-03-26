package com.dooingle.domain.dooinglecount.repository

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

    fun getHighCountDooinglers(size: Long): Set<String>? {
        return zSetOperations.reverseRange(KEY, 0, size - 1)
    }

    fun getAllCounts(): Set<String>? {
        return zSetOperations.reverseRange(KEY, 0, -1)
    }

    fun deleteAll() {
        zSetOperations.removeRange(KEY, 0, -1)
    }

    companion object {
        const val KEY = "dooingleCount"
    }

}