package com.moyoy.api.auth.jwt

import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import java.sql.Timestamp

/**
 *  TODO : 아직 저장소 고민중, 수정될 부분
 */

@Repository
class JwtRefreshWhiteListJDBCRepository(
    private val jdbcOperations: JdbcOperations
) {
    fun save(entity: JwtRefreshWhiteList) {
        jdbcOperations.update(INSERT_SQL) { ps ->
            ps.setLong(1, entity.userId)
            ps.setString(2, entity.tokenHash)
            ps.setTimestamp(3, Timestamp.valueOf(entity.expiresAt))
        }
    }

    fun existByTokenHash(tokenHash: String): Boolean {
        return jdbcOperations.queryForObject(EXISTS_SQL, Boolean::class.java, tokenHash) ?: false
    }

    fun deleteByTokenHash(tokenHash: String) {
        jdbcOperations.update(DELETE_SQL, tokenHash)
    }

    companion object {
        private val INSERT_SQL =
            """
            INSERT INTO jwt_refresh_token (user_id, token_hash, expires_at)
            VALUES (?, ?, ?)
            """.trimIndent()

        private val DELETE_SQL =
            """
            DELETE FROM jwt_refresh_token
            WHERE token_hash = ?
            """.trimIndent()

        private val EXISTS_SQL =
            """
            SELECT EXISTS (
                SELECT 1
                FROM jwt_refresh_token
                WHERE token_hash = ?
            )
            """.trimIndent()
    }
}
