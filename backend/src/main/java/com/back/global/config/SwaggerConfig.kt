package com.back.global.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Swagger 설정 클래스
// http://localhost:8080/swagger-ui/index.html 로 접근하여 Swagger UI를 확인할 수 있습니다.
// 아래 jwt 세팅은 JWT 토큰 인증을 위한 설정입니다.
@OpenAPIDefinition(
    info = Info(
        title = "PetMatching Project API 명세서",
        version = "v1",
        description = "PetMatching Project API 명세서입니다"
    ), 
    servers = [Server(url = "http://localhost:8080", description = "로컬 서버")]
)
@Configuration
class SwaggerConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        val apiKey = SecurityScheme().apply {
            type = SecurityScheme.Type.HTTP
            `in` = SecurityScheme.In.HEADER
            name = "Authorization"
            scheme = "bearer"
            bearerFormat = "JWT"
        }
        
        val securityRequirement = SecurityRequirement().apply {
            addList("Bearer Token")
        }

        return OpenAPI().apply {
            components = Components().addSecuritySchemes("Bearer Token", apiKey)
            addSecurityItem(securityRequirement)
        }
    }

    companion object {
        const val JWT_SECURITY_SCHEME: String = "JWT Token"
    }
}