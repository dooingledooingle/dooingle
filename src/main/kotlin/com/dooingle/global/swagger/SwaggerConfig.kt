package com.dooingle.global.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .addSecurityItem(
            SecurityRequirement().addList("Bearer Authentication")
        )
        .components(
            // TODO 인증 시 HttpOnly 쿠키를 사용하면서 Authorization 헤더를 사용하지 않고 있음, Swagger에서 이 부분이 굳이 필요한지 논의 필요
            //  어떤 원리인지는 모르겠으나, 프론트 서버에서 로그인하면 localhost:8080에서도 HttpOnly 쿠키를 사용할 수 있음
            Components().addSecuritySchemes(
                "Bearer Authentication",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
                    .`in`(SecurityScheme.In.HEADER)
                    .name("Authorization")
            )
        )
        .info(
            Info()
                .title("Dooingle API")
                .description("Dooingle API schema")
                .version("1.0.0")
        )
}
