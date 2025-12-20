package com.moyoy.api.user.application

import com.moyoy.api.auth.jwt.JwtPayloadExtractor
import com.moyoy.api.auth.jwt.JwtProvider
import com.moyoy.api.auth.jwt.JwtRefreshWhiteList
import com.moyoy.api.auth.jwt.JwtType
import com.moyoy.api.auth.jwt.JwtValidator
import com.moyoy.api.user.application.processor.RefreshTokenRotateProcessor
import com.moyoy.common.utils.HashUtils
import org.springframework.stereotype.Service

@Service
class ReIssueJwtUseCase(
    private val jwtProvider: JwtProvider,
    private val jwtPayloadExtractor: JwtPayloadExtractor,
    private val jwtValidator: JwtValidator,
    private val refreshTokenRotateProcessor: RefreshTokenRotateProcessor
) {
    data class Input(
        val refreshTokenRaw: String
    )

    data class Output(
        val accessToken: String,
        val refreshToken: String
    )

    fun execute(input: Input): Output {
        jwtValidator.validate(JwtType.REFRESH, input.refreshTokenRaw)
        val jwtUserDto = jwtPayloadExtractor.extractUserInfo(input.refreshTokenRaw)

        val reissuedRefreshToken = jwtProvider.createJwtToken(jwtUserDto, JwtType.REFRESH)
        val reissuedAccessToken = jwtProvider.createJwtToken(jwtUserDto, JwtType.ACCESS)

        val oldRefreshTokenHash = HashUtils.sha256Base64(input.refreshTokenRaw)
        val newRefreshTokenHash = HashUtils.sha256Base64(reissuedRefreshToken)
        val expirationTime = jwtPayloadExtractor.extractExpirationTime(reissuedRefreshToken)

        val reissuedRefreshTokenWhiteList = JwtRefreshWhiteList.of(jwtUserDto.userId, newRefreshTokenHash, expirationTime)

        refreshTokenRotateProcessor.rotate(oldRefreshTokenHash, reissuedRefreshTokenWhiteList)

        return Output(
            accessToken = reissuedAccessToken,
            refreshToken = reissuedRefreshToken
        )
    }
}
