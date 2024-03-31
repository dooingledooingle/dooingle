package com.dooingle.global.security

import com.dooingle.global.jwt.JwtAuthenticationFilter
import jakarta.servlet.DispatcherType
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
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
            .securityMatcher(NegatedRequestMatcher(EndpointRequest.toAnyEndpoint()))
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .headers { it.frameOptions { frameOptionsConfig -> frameOptionsConfig.sameOrigin() } }
            .authorizeHttpRequests {
                // (1) dispatcher type이 async인 경우 모두 허용 - CoyoteAdapter asyncDispatch access denied로 검색하여 https://github.com/spring-projects/spring-security/issues/11962 참고 후 임시 조치해둔 것
                // requestMatchers("/api/notification").permitAll()으로 할 수도 있었겠지만 이 쪽이 더 낫다고 판단함
                // cf. dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()가  requestMatchers("/api/**").authenticated()보다 뒤에 있는 경우에는 적용이 안 됨
                it.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                    // (2) 요청 url /api/** 패턴은 모두 인증 필요
                    .requestMatchers(
                        "/api/**",
                    ).authenticated()
                    // (3) 요청 url 아래 패턴은 모두 허용(로그인, 인증 서버 콜백 리다이렉트 url 등)
                    // TODO: "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**"는 개발 환경에서만 인증 불필요하게 만들기
                    .requestMatchers(
                        "/oauth2/login/**",
                        "/oauth2/callback/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/h2-console/**",
                    ).permitAll()
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

    @Bean
    fun actuatorFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }
}
