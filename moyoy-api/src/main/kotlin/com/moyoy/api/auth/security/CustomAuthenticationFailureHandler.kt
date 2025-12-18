package com.moyoy.api.auth.security

import com.moyoy.api.auth.error.AuthErrorCode
import com.moyoy.api.support.ErrorResponseHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

/**
 * OAuth를 이용한 사용자의 직접적인 인증 과정에서 발생한 에러 처리.
 * 에러 발생 지점: OAuth2LoginAuthenticationFilter가 상속하는 AbstractAuthenticationProcessingFilter
 */

@Component
class CustomAuthenticationFailureHandler(
    private val errorResponseHandler: ErrorResponseHandler
) : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        errorResponseHandler.writeErrorResponse(
            response,
            HttpStatus.UNAUTHORIZED.value(),
            AuthErrorCode.UNAUTHORIZED_USER
        )
    }
}
