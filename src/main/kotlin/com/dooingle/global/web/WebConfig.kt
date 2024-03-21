package com.dooingle.global.web

import com.dooingle.global.oauth2.provider.OAuth2ProviderConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(OAuth2ProviderConverter())
    }

    override fun addCorsMappings(registry: CorsRegistry) { /* TODO 이 부분은 나중에 SecurityConfig 쪽으로 옮기는 게 좋을 것 같습니다. */
        registry
            .addMapping("/**")
            .allowedOrigins("http://localhost:5173") // CORS 설정 - http://localhost:5173 origin 허용
            .allowedMethods(*ALLOWED_METHOD_NAMES.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
            .allowCredentials(true) // credentials 포함한 요청 허용
            .exposedHeaders(HttpHeaders.LOCATION)
    }

    companion object {
        const val ALLOWED_METHOD_NAMES: String = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH"
    }

}