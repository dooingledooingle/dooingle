package com.dooingle.global.security

import com.dooingle.global.jwt.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntrypoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
    @Value("\${frontend.domain}") private val frontendOrigin: String, // profile에 따라 허용되는 origin 달라지게 함
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .headers { it.frameOptions { frameOptionsConfig -> frameOptionsConfig.sameOrigin() } }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/**",
                ).authenticated()
                    .requestMatchers(
                        "/oauth2/login/**", "/oauth2/callback/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**",
                ).permitAll() // 로그인, 콜백 리다이렉트 url은 인증 불필요
            // TODO: "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**"는 개발 환경에서만 인증 불필요하게 만들기
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntrypoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .build()
    }

    private fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
            .also { it.allowCredentials = true }
            .also { it.allowedOrigins = listOf(frontendOrigin) }
            .also { it.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE") }
            .also { it.allowedHeaders = listOf("content-type") }
            // .also { it.exposedHeaders = listOf("*") }
            // 현 상태에서는 오류 발생하지 않아 주석 처리, 최소한만 허용함

        return UrlBasedCorsConfigurationSource()
            .also { it.registerCorsConfiguration("/**", configuration) }
    }
}