package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.jwt.dto.JwtUserClaims
import com.moyoy.common.const.JwtConst.CLAIM_AUTHORITY
import com.moyoy.common.const.JwtConst.CLAIM_TOKEN_TYPE
import com.moyoy.common.const.JwtConst.CLAIM_USER_ID
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.Date

class JwtProvider(
    private val macSigner: MACSigner,
    private val jwk: JWK,
    private val accessTokenExpirationMs: Long,
    private val refreshTokenExpirationMs: Long
) {
    fun createJwtToken(
        jwtUserClaims: JwtUserClaims,
        tokenType: JwtType
    ): String {
        val header =
            JWSHeader
                .Builder(jwk.algorithm as JWSAlgorithm)
                .keyID(jwk.keyID)
                .build()

        val payload =
            JWTClaimsSet
                .Builder()
                .claim(CLAIM_USER_ID, jwtUserClaims.userId)
                .claim(CLAIM_TOKEN_TYPE, tokenType.value)
                .claim(CLAIM_AUTHORITY, jwtUserClaims.authority)
                .expirationTime(getTokenExpiration(tokenType))
                .build()

        return SignedJWT(header, payload).apply { sign(macSigner) }.serialize()
    }

    private fun getTokenExpiration(tokenType: JwtType): Date {
        val expirationMillis =
            when (tokenType) {
                JwtType.ACCESS -> accessTokenExpirationMs
                JwtType.REFRESH -> refreshTokenExpirationMs
            }
        return Date(System.currentTimeMillis() + expirationMillis)
    }
}
