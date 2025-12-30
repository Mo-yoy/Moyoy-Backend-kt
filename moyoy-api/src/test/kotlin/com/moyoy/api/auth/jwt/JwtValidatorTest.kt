package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.error.BlockedJwtException
import com.moyoy.api.auth.error.ExpiredJwtException
import com.moyoy.api.auth.error.InvalidJwtException
import com.moyoy.api.auth.error.JwtNotExistException
import com.moyoy.api.auth.error.JwtTypeMismatchException
import com.moyoy.common.const.JwtConst
import com.moyoy.common.utils.HashUtils
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class JwtValidatorTest {
    @Autowired
    private lateinit var jwtValidator: JwtValidator

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var repository: JwtRefreshWhiteListJDBCRepository

    @Autowired
    private lateinit var jwtPayloadExtractor: JwtPayloadExtractor

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Test
    @DisplayName("유효한 ACCESS 토큰은 검증을 통과해야 한다")
    fun validate_access_token_success() {
        // given
        val userClaims = JwtUserClaims(100L, "ROLE_USER")
        val token = jwtProvider.createJwtToken(userClaims, JwtType.ACCESS)

        // when & then
        assertDoesNotThrow {
            jwtValidator.validate(JwtType.ACCESS, token)
        }
    }

    @Test
    @DisplayName("ACCESS 토큰 검증 시 타입이 ACCESS가 아니라면, JwtTypeMismatchException을 던져야 한다")
    fun validate_access_token_type_mismatch() {
        // given
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "USER"), JwtType.REFRESH)

        // when & then
        assertThrows<JwtTypeMismatchException> {
            jwtValidator.validate(JwtType.ACCESS, token)
        }
    }

    @DisplayName("토큰이 비어있거나(\"\"), 공백(\"  \"), 탭, 개행 등으로만 이루어진 경우 JwtNotExistException을 던져야 한다")
    @ParameterizedTest(name = "입력값: \"{0}\"")
    @ValueSource(strings = ["", "   ", "\t", "\n"])
    fun validate_blank_token_throws_exception(invalidToken: String) {
        // when & then
        assertThrows<JwtNotExistException> {
            jwtValidator.validate(JwtType.ACCESS, invalidToken)
        }
    }

    @Test
    @DisplayName("서명이 잘못된 토큰(다른 키로 서명)은 InvalidJwtException을 던져야 한다")
    fun validate_signature_fail() {
        // given
        val wrongSecret = "OTHER_KEY_OTHER_KEY_OTHER_KEY_OTHER_KEY_OTHER_KEY"
        val wrongJwk =
            OctetSequenceKey
                .Builder(wrongSecret.toByteArray())
                .algorithm(JWSAlgorithm.HS256)
                .build()
        val wrongSigner = MACSigner(wrongJwk)

        val token = createToken(JwtType.ACCESS, signer = wrongSigner)

        // when & then
        assertThrows<InvalidJwtException> {
            jwtValidator.validate(JwtType.ACCESS, token)
        }
    }

    @Test
    @DisplayName("만료된 ACCESS 토큰은 ExpiredJwtException을 던져야 한다")
    fun validate_access_token_expired() {
        // given
        val pastDate = Date(System.currentTimeMillis() - 10000)
        val token = createToken(JwtType.ACCESS, expirationTime = pastDate)

        // when & then
        assertThrows<ExpiredJwtException> {
            jwtValidator.validate(JwtType.ACCESS, token)
        }
    }

    @Test
    @DisplayName("유효하고 WhiteList에 존재하는 REFRESH 토큰은 검증을 통과해야 한다")
    fun validate_refresh_token_success() {
        // given
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "USER"), JwtType.REFRESH)
        val userId = 1L
        val tokenHash = HashUtils.sha256Base64(token)
        val expirationTime = jwtPayloadExtractor.extractExpirationTime(token)

        repository.save(
            JwtRefreshWhiteList(
                userId = userId,
                tokenHash = tokenHash,
                expiresAt = expirationTime
            )
        )

        // when & then
        assertDoesNotThrow {
            jwtValidator.validate(JwtType.REFRESH, token)
        }
    }

    @Test
    @DisplayName("유효한 Refresh 토큰이지만 WhiteList에 없으면 BlockedJwtException을 던져야 한다")
    fun validate_refresh_token_not_in_whitelist() {
        // given
        val token = jwtProvider.createJwtToken(JwtUserClaims(1L, "USER"), JwtType.REFRESH)

        // when & then
        assertThrows<BlockedJwtException> {
            jwtValidator.validate(JwtType.REFRESH, token)
        }
    }

    private fun createToken(
        type: JwtType,
        expirationTime: Date = Date(System.currentTimeMillis() + 100000),
        signer: MACSigner? = null
    ): String {
        val finalSigner =
            signer ?: run {
                val jwk =
                    OctetSequenceKey
                        .Builder(secretKey.toByteArray())
                        .algorithm(JWSAlgorithm.HS256)
                        .build()
                MACSigner(jwk)
            }

        val header = JWSHeader.Builder(JWSAlgorithm.HS256).build()

        val claims =
            JWTClaimsSet
                .Builder()
                .claim(JwtConst.CLAIM_TOKEN_TYPE, type.value)
                .claim(JwtConst.CLAIM_USER_ID, 100L)
                .claim(JwtConst.CLAIM_AUTHORITY, "ROLE_USER")
                .expirationTime(expirationTime)
                .build()

        return SignedJWT(header, claims).apply { sign(finalSigner) }.serialize()
    }
}
