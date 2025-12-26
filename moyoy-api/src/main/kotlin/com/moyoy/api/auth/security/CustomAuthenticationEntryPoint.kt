package com.moyoy.api.auth.security

import com.moyoy.api.auth.error.AuthErrorCode
import com.moyoy.api.support.ErrorResponseHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * 인증되지 않은 사용자가 인증이 필요한 자원에 접근할 때 인증 실패 에러 처리
 * 인증된 사용자지만 권한이 부족하면 AccessDeniedHandler 에서 처리
 *
 * 로그인 실패 -> Authentication Fail Handler
 * 인증되지 않은 사용자가 인증이 필요한 자원에 접근 -> AuthenticationEntryPoint
 */

@Component
class CustomAuthenticationEntryPoint(
    private val errorResponseHandler: ErrorResponseHandler
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        errorResponseHandler.writeErrorResponse(
            response,
            UNAUTHORIZED.value(),
            AuthErrorCode.UNAUTHORIZED_USER
        )
    }
}
