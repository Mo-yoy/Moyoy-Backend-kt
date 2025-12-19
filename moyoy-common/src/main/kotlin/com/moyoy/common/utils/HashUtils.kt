package com.moyoy.common.utils

import java.security.MessageDigest
import java.util.Base64

object HashUtils {
    private const val ALGORITHM = "SHA-256"

    fun sha256Base64(raw: String): String =
        runCatching {
            val md = MessageDigest.getInstance(ALGORITHM)
            val digest = md.digest(raw.toByteArray(Charsets.UTF_8))
            Base64.getEncoder().encodeToString(digest)
        }.getOrElse { e ->
            throw IllegalStateException("SHA-256 해시 중 에러 발생", e)
        }
}
