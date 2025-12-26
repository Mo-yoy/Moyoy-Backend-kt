package com.moyoy.api.auth.jwt

enum class JwtType(
    val value: String
) {
    ACCESS("access"),
    REFRESH("refresh")
}
