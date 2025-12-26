package com.moyoy.infra.jasypt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jasypt.encryptor")
data class JasyptProperties(
    val password: String,
    val algorithm: String,
    val poolSize: String
)
