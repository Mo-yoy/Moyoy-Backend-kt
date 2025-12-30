package com.moyoy.api.user.presentation

import com.moyoy.api.auth.jwt.RefreshTokenCookieFactory
import com.moyoy.api.support.response.ApiResponse
import com.moyoy.api.user.application.ReIssueJwtUseCase
import com.moyoy.api.user.presentation.dto.response.JwtReissueResponse
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val refreshTokenCookieFactory: RefreshTokenCookieFactory,
    private val reIssueJwtUseCase: ReIssueJwtUseCase
) {
    companion object {
        const val REFRESH_TOKEN_COOKIE_NAME = "refresh"
    }

    @PostMapping("/auth/reissue/token")
    fun reissueJwt(
        @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") refreshTokenRaw: String
    ): ResponseEntity<ApiResponse<JwtReissueResponse>> {
        val input = ReIssueJwtUseCase.Input(refreshTokenRaw)
        val output = reIssueJwtUseCase.execute(input)

        val refreshTokenCookie = refreshTokenCookieFactory.createRefreshTokenCookie(output.refreshToken).toString()
        val responseBody = JwtReissueResponse(output.accessToken)

        return ResponseEntity
            .status(HttpStatus.OK.value())
            .header(SET_COOKIE, refreshTokenCookie)
            .body(ApiResponse.success(responseBody))
    }
}
