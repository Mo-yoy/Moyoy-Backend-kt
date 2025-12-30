package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.jwt.dto.JwtUserClaims
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.OctetSequenceKey
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class JwtPayloadExtractorTest {
    private val secret = "ASGWKOMOMOMO!amfom326y,dopdmbdmgey4yh4hnf!!"
    private val accessTokenExpirationMs = 1000 * 60 * 10L // 10 분

    private val jwtProvider = createJwtProvider()
    private val jwtPayloadExtractor = JwtPayloadExtractor()

    @Test
    @DisplayName("JWT payload에 들어있는 사용자 정보(userId, authority)를 올바르게 추출해야 한다")
    fun extract_user_info_success() {
        // given
        val userClaims = JwtUserClaims(userId = 100L, authority = "ROLE_USER")
        val token = jwtProvider.createJwtToken(userClaims, JwtType.ACCESS)

        // when
        val extractedUserClaims = jwtPayloadExtractor.extractUserClaims(token)

        // then
        assertThat(extractedUserClaims.userId).isEqualTo(100L)
        assertThat(extractedUserClaims.authority).isEqualTo("ROLE_USER")
    }

    @Test
    @DisplayName("토큰에서 만료 시간을 LocalDateTime 으로 올바르게 변환하여 추출해야 한다")
    fun extract_expiration_time_success() {
        // given
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "USER"), JwtType.ACCESS)

        // when
        val extractedTime = jwtPayloadExtractor.extractExpirationTime(token)

        // then
        val expectedTime = LocalDateTime.now().plusNanos(accessTokenExpirationMs * 1_000_000)
        assertThat(extractedTime).isCloseTo(expectedTime, within(10, ChronoUnit.SECONDS))
    }

    private fun createJwtProvider(): JwtProvider {
        val jwk =
            OctetSequenceKey
                .Builder(secret.toByteArray())
                .algorithm(JWSAlgorithm.HS256)
                .keyID("test_key")
                .build()

        return JwtProvider(
            macSigner = MACSigner(jwk),
            jwk = jwk,
            accessTokenExpirationMs = accessTokenExpirationMs,
            refreshTokenExpirationMs = 1000 * 60 * 60 * 24
        )
    }
}
