package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.error.InvalidJwtException
import com.nimbusds.jwt.SignedJWT
import java.text.ParseException

object JwtUtils {
    fun decode(rawToken: String): SignedJWT {
        return try {
            SignedJWT.parse(rawToken)
        } catch (e: ParseException) {
            throw InvalidJwtException()
        }
    }
}
