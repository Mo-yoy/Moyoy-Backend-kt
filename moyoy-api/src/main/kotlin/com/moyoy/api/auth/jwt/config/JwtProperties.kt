package com.moyoy.api.auth.jwt.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val keyId: String,
    val accessExpirationMs: Long,
    val refreshExpirationMs: Long
)
