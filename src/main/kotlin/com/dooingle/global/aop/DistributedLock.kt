package com.dooingle.global.aop

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class DistributedLock(
    private val redissonClient : RedissonClient
) {

    operator fun <T> invoke(
        key: String,
        waitTime: Duration = Duration.ofSeconds(5),
        leaseTime: Duration = Duration.ofSeconds(3),
        func: () -> T
    ): T {
        val lock = redissonClient.getLock("REDISSON_LOCK:$key")
        if (!lock.tryLock(waitTime.seconds, leaseTime.seconds, TimeUnit.SECONDS)) {
            throw RuntimeException("Lock 획득 대기시간 만료")
        }
        return func().also { lock.unlock() }
    }
}