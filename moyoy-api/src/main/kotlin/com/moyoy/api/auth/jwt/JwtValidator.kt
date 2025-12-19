package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.error.BlockedJwtException
import com.moyoy.api.auth.error.ExpiredJwtException
import com.moyoy.api.auth.error.InvalidJwtException
import com.moyoy.api.auth.error.JwtNotExistException
import com.moyoy.api.auth.error.JwtTypeMismatchException
import com.moyoy.common.const.JwtConst
import com.moyoy.common.utils.HashUtils
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtValidator(
    private val jwtPayloadExtractor: JwtPayloadExtractor,
    private val macVerifier: MACVerifier,
    private val jwtRefreshWhiteListJDBCRepository: JwtRefreshWhiteListJDBCRepository
) {
    fun validate(
        tokenType: JwtType,
        rawToken: String
    ) {
        when (tokenType) {
            JwtType.REFRESH -> validateRefresh(rawToken)
            JwtType.ACCESS -> validateAccess(rawToken)
        }
    }

    private fun validateRefresh(rawToken: String) {
        validateJwt(JwtType.REFRESH, rawToken)
        validateTokenExistsInWhiteList(rawToken)
    }

    private fun validateAccess(rawToken: String) {
        validateJwt(JwtType.ACCESS, rawToken)
    }

    private fun validateJwt(
        tokenType: JwtType,
        rawToken: String
    ) {
        validateTokenNotExist(rawToken)

        val signedJWT = JwtDecoder.decode(rawToken)
        validateTokenSignature(signedJWT)

        val claimsSet = signedJWT.jwtClaimsSet
        validateTokenType(tokenType, claimsSet)
        validateTokenExpiration(claimsSet)
    }

    private fun validateTokenNotExist(rawToken: String) {
        if (rawToken.isBlank()) throw JwtNotExistException()
    }

    private fun validateTokenSignature(signedJWT: SignedJWT) {
        val verifyResult =
            try {
                signedJWT.verify(macVerifier)
            } catch (e: JOSEException) {
                throw InvalidJwtException()
            }

        if (!verifyResult) throw InvalidJwtException()
    }

    private fun validateTokenType(
        tokenType: JwtType,
        claimsSet: JWTClaimsSet
    ) {
        val claimTokenType = claimsSet.getClaim(JwtConst.CLAIM_TOKEN_TYPE)?.toString()

        if (claimTokenType == null || claimTokenType != tokenType.value) {
            throw JwtTypeMismatchException()
        }
    }

    private fun validateTokenExpiration(claimsSet: JWTClaimsSet) {
        val exp = claimsSet.expirationTime

        if (exp == null || exp.before(Date())) {
            throw ExpiredJwtException()
        }
    }

    private fun validateTokenExistsInWhiteList(rawToken: String) {
        val tokenHash = HashUtils.sha256Base64(rawToken)

        if (!jwtRefreshWhiteListJDBCRepository.existByTokenHash(tokenHash)) {
            throw BlockedJwtException()
        }
    }
}
