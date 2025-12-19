package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.error.InvalidJwtException
import com.moyoy.common.const.JwtConst.CLAIM_AUTHORITY
import com.moyoy.common.const.JwtConst.CLAIM_USER_ID
import com.nimbusds.jwt.JWTClaimsSet
import org.springframework.stereotype.Component
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class JwtPayloadExtractor {
    fun extractUserInfo(rawToken: String): JwtUserDto {
        val claims = getClaims(rawToken)
        val userId = claims.getClaim(CLAIM_USER_ID) as Long
        val authority = claims.getClaim(CLAIM_AUTHORITY).toString()
        return JwtUserDto(userId, authority)
    }

    fun extractExpirationTime(rawToken: String): LocalDateTime {
        val claims = getClaims(rawToken)
        return claims.expirationTime
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    private fun getClaims(rawToken: String): JWTClaimsSet {
        val signedJWT = JwtUtils.decode(rawToken)
        return try {
            signedJWT.jwtClaimsSet
        } catch (e: ParseException) {
            throw InvalidJwtException()
        }
    }
}
