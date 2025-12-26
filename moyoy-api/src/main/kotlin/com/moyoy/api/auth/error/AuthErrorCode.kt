package com.moyoy.api.auth.error

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) : BaseErrorCode {
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "LOGIN_401_1", "인증 헤더나 쿠키에 JWT 토큰이 존재 하지 않습니다."),
    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, "LOGIN_401_2", "토큰 타입이 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN_401_3", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN_401_4", "만료된 토큰입니다."),
    BLOCKED_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN_401_5", "블랙리스트 처리된 토큰 입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "LOGIN_401_6", "인증에 실패한 사용자입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "LOGIN_403_1", "사용자의 권한이 부족합니다.");

    override val errorReason: ErrorReason
        get() = ErrorReason(httpStatus.value(), code, message)
}
