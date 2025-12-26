package com.moyoy.api.auth.jwt

import com.moyoy.common.const.JwtConst
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jwt.SignedJWT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Date

class JwtProviderTest {
    private val secret = "ASGWKOMOMOMO!amfom326y,dopdmbdmgey4yh4hnf!!"
    private val accessTokenMs = 1000L * 60 * 10 // 10분
    private val refreshTokenMs = 1000L * 60 * 60 * 24 // 24시간

    @Test
    @DisplayName("JWT 생성 시 Header, Payload, Signature가 정상 생성 되어야 한다.")
    fun create_jwt_token_verification() {
        // given
        val keyId = "2025_test_key"
        val jwtProvider = createJwtProvider(keyId = keyId)
        val userDto = JwtUserClaims(userId = 100L, authority = "ROLE_USER")

        // when
        val tokenString = jwtProvider.createJwtToken(userDto, JwtType.ACCESS)

        // then
        val signedJWT = SignedJWT.parse(tokenString)

        assertThat(signedJWT.header.algorithm).isEqualTo(JWSAlgorithm.HS256)
        assertThat(signedJWT.header.keyID).isEqualTo(keyId)

        val claims = signedJWT.jwtClaimsSet
        assertThat(claims.getClaim(JwtConst.CLAIM_USER_ID)).isEqualTo(100L)
        assertThat(claims.getStringClaim(JwtConst.CLAIM_TOKEN_TYPE)).isEqualTo(JwtType.ACCESS.value)
        assertThat(claims.getStringClaim(JwtConst.CLAIM_AUTHORITY)).isEqualTo("ROLE_USER")

        val now = Date()
        val expectedExpirationTime = now.time + accessTokenMs
        assertThat(claims.expirationTime).isAfter(now)
        assertThat(claims.expirationTime.time).isCloseTo(expectedExpirationTime, Offset.offset(10 * 1000L))

        val verifier = MACVerifier(secret.toByteArray())
        assertThat(signedJWT.verify(verifier)).isTrue()
    }

    @Test
    @DisplayName("ACCESS 타입으로 생성 요청 시, 페이로드에 ACCESS 타입이 명시되고 Access Token의 만료 시간이 적용되어야 한다")
    fun create_jwt_with_access_type_logic() {
        // given
        val jwtProvider = createJwtProvider()
        val now = System.currentTimeMillis()

        // when
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "ROLE_USER"), JwtType.ACCESS)

        // then
        val claims = SignedJWT.parse(token).jwtClaimsSet

        assertThat(claims.getStringClaim(JwtConst.CLAIM_TOKEN_TYPE)).isEqualTo(JwtType.ACCESS.value)
        assertThat(claims.expirationTime.time).isCloseTo(now + accessTokenMs, Offset.offset(10000L))
    }

    @Test
    @DisplayName("REFRESH 타입으로 생성 요청 시, 페이로드에 REFRESH 타입이 명시되고 Refresh Token의 만료 시간이 적용되어야 한다")
    fun create_jwt_with_refresh_type_logic() {
        // given
        val jwtProvider = createJwtProvider()
        val now = System.currentTimeMillis()

        // when
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "ROLE_USER"), JwtType.REFRESH)

        // then
        val claims = SignedJWT.parse(token).jwtClaimsSet

        assertThat(claims.getStringClaim(JwtConst.CLAIM_TOKEN_TYPE)).isEqualTo(JwtType.REFRESH.value)
        assertThat(claims.expirationTime.time).isCloseTo(now + refreshTokenMs, Offset.offset(10000L))
    }

    private fun createJwtProvider(
        keyId: String = "test_key",
        accessTokenExpirationMs: Long = accessTokenMs,
        refreshTokenExpirationMs: Long = refreshTokenMs
    ): JwtProvider {
        val jwk =
            OctetSequenceKey
                .Builder(secret.toByteArray())
                .algorithm(JWSAlgorithm.HS256)
                .keyID(keyId)
                .build()

        return JwtProvider(
            macSigner = MACSigner(jwk),
            jwk = jwk,
            accessTokenExpirationMs = accessTokenExpirationMs,
            refreshTokenExpirationMs = refreshTokenExpirationMs
        )
    }
}
