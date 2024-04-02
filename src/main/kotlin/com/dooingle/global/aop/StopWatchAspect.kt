package com.dooingle.global.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
@Aspect
class StopWatchAspect {

    private val logger = LoggerFactory.getLogger("Execution Time Logger")

    @Around("@annotation(com.dooingle.global.aop.StopWatch)")
    fun run(joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()

        stopWatch.start()
        val result = joinPoint.proceed()
        stopWatch.stop()

        val methodName = joinPoint.signature.name
        val methodArguments = joinPoint.args

        val timeElapsedMs = stopWatch.totalTimeMillis
        logger.info("Method Name: $methodName | Arguments: ${methodArguments.joinToString(", ")} | Execution Time: ${timeElapsedMs}ms")

        return result
    }
}