package com.moyoy.api.user.application

import com.moyoy.api.auth.jwt.JwtPayloadExtractor
import com.moyoy.api.auth.jwt.JwtProvider
import com.moyoy.api.auth.jwt.JwtRefreshWhiteList
import com.moyoy.api.auth.jwt.JwtRefreshWhiteListJDBCRepository
import com.moyoy.api.auth.jwt.JwtType
import com.moyoy.api.auth.jwt.JwtValidator
import com.moyoy.common.utils.HashUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReIssueJwtUseCase(
    private val jwtProvider: JwtProvider,
    private val jwtPayloadExtractor: JwtPayloadExtractor,
    private val jwtValidator: JwtValidator,
    private val jwtRefreshWhiteListJDBCRepository: JwtRefreshWhiteListJDBCRepository
) {
    data class Input(
        val refreshTokenRaw: String
    )

    data class Output(
        val accessToken: String,
        val refreshToken: String
    )

    // / TODO 트랜잭션 범위 좁히기
    @Transactional
    fun execute(input: Input): Output {
        jwtValidator.validate(JwtType.REFRESH, input.refreshTokenRaw)
        val jwtUserDto = jwtPayloadExtractor.extractUserInfo(input.refreshTokenRaw)

        val reissuedRefreshToken = jwtProvider.createJwtToken(jwtUserDto, JwtType.REFRESH)
        val reissuedAccessToken = jwtProvider.createJwtToken(jwtUserDto, JwtType.ACCESS)

        val oldRefreshTokenHash = HashUtils.sha256Base64(input.refreshTokenRaw)
        val newRefreshTokenHash = HashUtils.sha256Base64(reissuedRefreshToken)
        val expirationTime = jwtPayloadExtractor.extractExpirationTime(reissuedRefreshToken)

        val reissuedRefreshTokenWhiteList = JwtRefreshWhiteList.of(jwtUserDto.userId, newRefreshTokenHash, expirationTime)

        // 실제 트랜잭션이 필요한 곳
        jwtRefreshWhiteListJDBCRepository.deleteByTokenHash(oldRefreshTokenHash)
        jwtRefreshWhiteListJDBCRepository.save(reissuedRefreshTokenWhiteList)

        return Output(
            accessToken = reissuedAccessToken,
            refreshToken = reissuedRefreshToken
        )
    }
}
