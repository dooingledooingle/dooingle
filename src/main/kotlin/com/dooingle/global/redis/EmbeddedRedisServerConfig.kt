package com.dooingle.global.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer

@Profile("local-memory-h2", "test")
@Configuration
class EmbeddedRedisServerConfig {

    val redisServer = RedisServer()

    @PostConstruct
    fun startRedis() {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }
}
