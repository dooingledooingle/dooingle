package com.dooingle.global.jwt

import com.dooingle.global.security.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtHelper: JwtHelper
) : OncePerRequestFilter() {

    /*
    // 기존 코드 authorization 헤더 관련 정규식
    companion object {
        private val BEARER_PATTERN = Regex("^Bearer (.+?)$")
    }
     */

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // val jwt = request.getBearerToken() // 기존 코드 authorization 헤더에서 토큰 가져오는 메서드 호출
        val jwt = extractJwtFromCookie(request)

        jwt?.run {
            jwtHelper.validateToken(jwt)
                .onSuccess {
                    val userId = it.payload.subject.toLong()
                    val role = it.payload.get("role", String::class.java)

                    val principal = UserPrincipal(
                        id = userId,
                        roles = setOf(role)
                    )

                    val authentication = JwtAuthenticationToken(
                        principal = principal,
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    )
                    SecurityContextHolder.getContext().authentication = authentication
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
        filterChain.doFilter(request, response)
    }

    /*
    // 기존 코드 authorization 헤더 이용
    private fun HttpServletRequest.getBearerToken(): String? {
        return this.getHeader(HttpHeaders.AUTHORIZATION)
            ?.let { BEARER_PATTERN.find(it)?.groupValues?.get(1) }
    }
    */

    private fun extractJwtFromCookie(request: HttpServletRequest): String? {
        return request.cookies?.firstOrNull { it.name == "accessToken" }?.value
    }

}