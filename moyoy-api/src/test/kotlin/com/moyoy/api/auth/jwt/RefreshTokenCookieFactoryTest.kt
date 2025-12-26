package com.moyoy.api.auth.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class RefreshTokenCookieFactoryTest {
    private val refreshMs = 3600000L
    private val domain = "moyoy.com"
    private val sameSite = "Lax"
    private val factory = RefreshTokenCookieFactory(refreshMs, domain, sameSite)

    @Test
    @DisplayName("설정된 값들에 따라 리프레시 토큰 쿠키가 올바르게 생성된다")
    fun create_cookie_success() {
        // given
        val token = "test-refresh-token"

        // when
        val cookie = factory.createRefreshTokenCookie(token)

        // then
        assertThat(cookie.name).isEqualTo("refresh")
        assertThat(cookie.value).isEqualTo(token)
        assertThat(cookie.isHttpOnly).isTrue()
        assertThat(cookie.isSecure).isTrue()
        assertThat(cookie.sameSite).isEqualTo("Lax")
        assertThat(cookie.domain).isEqualTo("moyoy.com")

        assertThat(cookie.maxAge.seconds).isEqualTo(3540)
    }
}
