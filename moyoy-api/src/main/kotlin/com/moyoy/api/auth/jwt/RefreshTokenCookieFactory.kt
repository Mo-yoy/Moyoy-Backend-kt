package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.jwt.JwtType.REFRESH
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RefreshTokenCookieFactory(
    @Value("\${spring.jwt.refresh-ms}") private val refreshMs: Long,
    @Value("\${spring.cookie.domain}") private val domain: String,
    @Value("\${spring.cookie.samesite:Strict}") private val sameSite: String
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
