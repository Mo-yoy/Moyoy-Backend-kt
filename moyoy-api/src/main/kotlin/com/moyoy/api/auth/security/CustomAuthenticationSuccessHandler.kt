package com.moyoy.api.auth.security

import com.moyoy.api.auth.jwt.JwtPayloadExtractor
import com.moyoy.api.auth.jwt.JwtProvider
import com.moyoy.api.auth.jwt.JwtRefreshWhiteList
import com.moyoy.api.auth.jwt.JwtRefreshWhiteListJDBCRepository
import com.moyoy.api.auth.jwt.JwtType
import com.moyoy.api.auth.jwt.JwtUserClaims
import com.moyoy.api.auth.jwt.RefreshTokenCookieFactory
import com.moyoy.common.utils.HashUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val cookieFactory: RefreshTokenCookieFactory,
    private val jwtProvider: JwtProvider,
    private val jwtPayloadExtractor: JwtPayloadExtractor,
    private val jwtRefreshTokenRepository: JwtRefreshWhiteListJDBCRepository,
    @Value("\${login.default-uri}") private val frontLoginSuccessURI: String
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val jwtUserClaims = JwtUserClaims.from(authentication)
        val refreshToken = jwtProvider.createJwtToken(jwtUserClaims, JwtType.REFRESH)
        response.addHeader(SET_COOKIE, cookieFactory.createRefreshTokenCookie(refreshToken).toString())

        val userId = jwtUserClaims.userId
        val expirationTime = jwtPayloadExtractor.extractExpirationTime(refreshToken)
        val refreshTokenHash = HashUtils.sha256Base64(refreshToken)

        jwtRefreshTokenRepository.save(
            JwtRefreshWhiteList.of(userId, refreshTokenHash, expirationTime)
        )

        /**
         *   스프린트 1의 요구사항을 반영하여 GitHub OAuth 인증 후 사용자를 무조건 Default URI로 리다이렉트 함.
         *   추후, 요구사항에 따라서 쿠키에 Redirect 경로를 추가해 처리하거나 Request Cache를 사용해서 기능을 확장할 예정
         */
        response.sendRedirect(frontLoginSuccessURI)
    }
}
