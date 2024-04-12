package com.dooingle.global.aop

import org.springframework.transaction.annotation.Transactional

open class TransactionForTrailingLambda {

    @Transactional
    open operator fun <T> invoke(
        func: () -> T
    ): T {
        return func()
    }
}
