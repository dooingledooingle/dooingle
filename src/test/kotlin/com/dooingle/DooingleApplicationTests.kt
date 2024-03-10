package com.dooingle

import io.kotest.core.spec.style.AnnotationSpec
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class DooingleApplicationTests : AnnotationSpec() {

    @Test
    fun contextLoads() {
        println("Spring context 정상 동작 확인")
    }
}