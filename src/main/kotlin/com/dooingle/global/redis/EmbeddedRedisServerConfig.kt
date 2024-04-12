package com.dooingle.global.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.commons.lang3.StringUtils
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.InputStreamReader


@Profile("test")
@Configuration
class EmbeddedRedisServerConfig {

    lateinit var redisServer: RedisServer
    var redisPort: Int = 10000

    @PostConstruct
    fun startRedis() {
        // 참고 https://jojoldu.tistory.com/297 및 그 댓글, https://lelecoder.com/120
        val availablePort = if (isRedisRunning()) findAvailablePort() else redisPort
        this.redisServer = RedisServer(availablePort)

        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    /**
     * Embedded Redis가 현재 실행중인지 확인
     */
    private fun isRedisRunning(): Boolean {
        return isRunning(executeGrepProcessCommand(redisPort))
    }

    /**
     * 현재 PC/서버에서 사용가능한 포트 조회
     */
    fun findAvailablePort(): Int {
        for (port in 10000..65535) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }

        throw IllegalArgumentException("Not Found Available port: 10000 ~ 65535")
    }

    /**
     * 해당 port를 사용중인 프로세스 확인하는 sh 실행
     */
    private fun executeGrepProcessCommand(port: Int): Process {
        val os = System.getProperty("os.name")

        val command: String
        val shell: Array<String>

        if (os.contains("win") || os.contains("Win")) {
            command = java.lang.String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port)
            shell = arrayOf("cmd.exe", "/y", "/c", command)
        } else {
            command = String.format("netstat -nat | grep LISTEN|grep %d", port)
            shell = arrayOf("/bin/sh", "-c", command)
        }

        return Runtime.getRuntime().exec(shell)
    }

    /**
     * 해당 Process가 현재 실행중인지 확인
     */
    private fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()

        try {
            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                while ((input.readLine().also {
                        line = it
                    }) != null) {
                    pidInfo.append(line)
                }
            }
        } catch (_: Exception) {
        }

        return !StringUtils.isEmpty(pidInfo.toString())
    }
}
