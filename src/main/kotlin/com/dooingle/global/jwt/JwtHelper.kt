package com.dooingle.global.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

@Component
class JwtHelper(
    @Value("\${jwt.secret_key}") private val secretKey: String,
    @Value("\${jwt.issuer}") private val issuer: String,
    @Value("\${jwt.access_token_expiration_hour}") private val accessTokenExpirationHour: Long,
) {

    private val key by lazy {
        val encodedKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
        Keys.hmacShaKeyFor(encodedKey.toByteArray())
    }

    fun generateAccessToken(id: Long, role: String): String {
        return generateToken(id, role, Duration.ofHours(accessTokenExpirationHour))
    }

    fun generateToken(id: Long, role: String, expirationPeriod: Duration): String {
        val claims = Jwts.claims().add(mapOf("role" to role)).build()

        val now = ZonedDateTime.now().toInstant()
        return Jwts.builder()
            .subject(id.toString())
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationPeriod)))
            .claims(claims)
            .signWith(key)
            .compact()
    }

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)
        }
    }

}
