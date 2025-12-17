package com.moyoy.api.support.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {
    @Value("\${spring.cors.allow-origin}")
    private lateinit var allowOrigins: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(*allowOrigins.split(",").toTypedArray())
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders(AUTHORIZATION)
            .allowCredentials(true)
    }
}
