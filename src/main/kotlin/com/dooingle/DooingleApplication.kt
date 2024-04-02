package com.dooingle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DooingleApplication

fun main(args: Array<String>) {
    runApplication<DooingleApplication>(*args)
}
