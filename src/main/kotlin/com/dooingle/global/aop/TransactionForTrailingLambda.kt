package com.dooingle.global.aop

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionForTrailingLambda {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    operator fun <T> invoke(
        func: () -> T
    ): T {
        return func()
    }
}