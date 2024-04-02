package com.dooingle.global.jwt

import com.dooingle.domain.user.model.UserRole
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JwtHelperTest {

    private val jwtHelper = JwtHelper(SECRET_KEY, ISSUER, TOKEN_EXPIRATION_HOUR)

    @Test
    fun `토큰 발급 후 검증 시 발급한 내용과 같다`() {
        // GIVEN
        val id: Long = 1
        val role = UserRole.USER

        // WHEN
        val token = jwtHelper.generateAccessToken(id, role.toString())
        val jwts = jwtHelper.validateToken(token)

        // THEN
        jwts.onSuccess {
            it.payload.subject.toLong() shouldBe id
            it.payload.get("role", String::class.java) shouldBe role.toString()
            it.payload.issuer shouldBe ISSUER
        }
    }

    companion object {
        const val SECRET_KEY = "test123456789test123456789test123456789test123456789test123456789test123456789"
        const val ISSUER = "dooingle.com"
        const val TOKEN_EXPIRATION_HOUR: Long = 1
    }

}