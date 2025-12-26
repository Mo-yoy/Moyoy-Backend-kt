package com.moyoy.api.auth.security

import com.moyoy.api.auth.error.AuthErrorCode
import com.moyoy.api.support.ErrorResponseHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

/**
 *  인증은 성공했으나 권한이 없을 경우 예외 처리
 */

@Component
class CustomAccessDeniedHandler(
    private val errorResponseHandler: ErrorResponseHandler
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        errorResponseHandler.writeErrorResponse(
            response,
            HttpStatus.FORBIDDEN.value(),
            AuthErrorCode.ACCESS_DENIED
        )
    }
}
