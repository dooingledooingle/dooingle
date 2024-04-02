package com.dooingle.global.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Profile("local-memory-h2", "test")
@DependsOn("embeddedRedisServerConfig")
@Configuration
class EmbeddedRedisClientConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
) {

    @Primary
    @Bean
    fun redissonClientOfEmbeddedRedis(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$host:$port")
        return Redisson.create(config)
    }

    @Primary
    @Bean
    fun redisConnectionFactoryOfEmbeddedRedis(redissonClient: RedissonClient): RedisConnectionFactory {
        return RedissonConnectionFactory(redissonClient)
    }

    @Primary
    @Bean
    fun redisTemplateOfEmbeddedRedis(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {

        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = redisConnectionFactory

        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()

        return redisTemplate
    }
}
