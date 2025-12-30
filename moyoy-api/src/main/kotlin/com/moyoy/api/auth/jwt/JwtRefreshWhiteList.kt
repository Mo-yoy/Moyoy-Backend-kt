package com.moyoy.api.auth.jwt

import java.time.LocalDateTime

/**
 * JWT Refresh Token WhiteList 모델
 * - JDBC로 직접 관리
 * - Redis에 보관도 고려 (서버 비용 관련 해서 고민중)
 */
data class JwtRefreshWhiteList(
    val id: Long? = null,
    val userId: Long,
    val tokenHash: String,
    val expiresAt: LocalDateTime
)
