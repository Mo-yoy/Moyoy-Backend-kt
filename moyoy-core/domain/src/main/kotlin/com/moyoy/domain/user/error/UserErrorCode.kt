package com.moyoy.domain.user.error

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) : BaseErrorCode {
    NOT_ALLOWED_GITHUB_ACCOUNT_TYPE(
        HttpStatus.BAD_REQUEST,
        "USER_400_1",
        "지원되지 않는 GitHub 계정 유형입니다. 개인 계정(User)만 사용 가능합니다."
    ),
    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "USER_404_1",
        "존재하지 않는 사용자 입니다."
    ),
    USER_GITHUB_TOKEN_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "USER_404_2",
        "사용자의 깃허브 토큰이 존재하지 않습니다."
    );

    override val errorReason: ErrorReason
        get() = ErrorReason(httpStatus.value(), code, message)
}
