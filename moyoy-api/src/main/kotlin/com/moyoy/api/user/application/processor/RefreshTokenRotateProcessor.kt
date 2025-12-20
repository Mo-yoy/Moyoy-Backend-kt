package com.moyoy.api.user.application.processor

import com.moyoy.api.auth.jwt.JwtRefreshWhiteList
import com.moyoy.api.auth.jwt.JwtRefreshWhiteListJDBCRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RefreshTokenRotateProcessor(
    private val jwtRefreshWhiteListJDBCRepository: JwtRefreshWhiteListJDBCRepository
) {
    @Transactional
    fun rotate(
        oldTokenHash: String,
        newTokenEntity: JwtRefreshWhiteList
    ) {
        jwtRefreshWhiteListJDBCRepository.deleteByTokenHash(oldTokenHash)
        jwtRefreshWhiteListJDBCRepository.save(newTokenEntity)
    }
}
