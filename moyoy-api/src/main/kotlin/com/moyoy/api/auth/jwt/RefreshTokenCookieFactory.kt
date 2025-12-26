package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.jwt.JwtType.REFRESH
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RefreshTokenCookieFactory(
    @Value("\${jwt.refresh-expiration-ms}") private val refreshMs: Long,
    @Value("\${cookie.domain}") private val domain: String,
    @Value("\${cookie.samesite}") private val sameSite: String
) {
    fun createRefreshTokenCookie(refreshToken: String): ResponseCookie {
        val maxAge = Duration.ofMillis(refreshMs).minusMinutes(1)

        return ResponseCookie
            .from(REFRESH.value, refreshToken)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite(sameSite)
            .domain(domain)
            .maxAge(maxAge)
            .build()
    }
}
