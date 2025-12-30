package com.moyoy.api.auth.jwt.config

import com.moyoy.api.auth.jwt.JwtProvider
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.util.Base64URL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    private val jwtProperties: JwtProperties
) {
    private val jwtSecret =
        SecretKeySpec(
            jwtProperties.secret.toByteArray(Charsets.UTF_8),
            JWSAlgorithm.HS256.name
        )

    @Bean
    fun jwtProvider(): JwtProvider {
        return JwtProvider(
            macSigner = macSigner(),
            jwk = octetSequenceKey(),
            accessTokenExpirationMs = jwtProperties.accessExpirationMs,
            refreshTokenExpirationMs = jwtProperties.refreshExpirationMs
        )
    }

    @Bean
    fun macSigner(): MACSigner {
        return MACSigner(jwtSecret)
    }

    @Bean
    fun macVerifier(): MACVerifier {
        return MACVerifier(jwtSecret)
    }

    /**
     *  Key Rolling 대비 keyId Header에 삽입
     *  Header의 알고리즘 변경을 이용한 공격방지를 위해 여기서 고정
     */
    @Bean
    fun octetSequenceKey(): OctetSequenceKey {
        val secretBytes = jwtSecret.encoded

        return OctetSequenceKey
            .Builder(Base64URL.encode(secretBytes))
            .keyID(jwtProperties.keyId)
            .algorithm(JWSAlgorithm.HS256)
            .build()
    }
}
