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

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods(*ALLOWED_METHOD_NAMES.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
            .exposedHeaders(HttpHeaders.LOCATION)
    }

    companion object {
        const val ALLOWED_METHOD_NAMES: String = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH"
    }

}