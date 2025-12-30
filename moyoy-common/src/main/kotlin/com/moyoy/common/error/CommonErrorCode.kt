package com.moyoy.common.error

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) : BaseErrorCode {
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "COMMON_400_1", "파라미터를 다시 확인해 주세요."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404_1", "리소스를 찾을 수 없습니다."),
    NOT_ALLOWED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405_1", "허용되지 않은 메서드 입니다."),
    TOO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "COMMON_429_1", "너무 많은 API를 호출했습니다. 나중에 재시도 해 주세요"),
    UNKNOWN_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_1", "서버 내부에서 알 수 없는 에러가 발생했습니다. 관리자에게 문의해 주세요."),
    HTTP_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_2", "API 서버와 외부 서버 통신 중 에러가 발생했습니다.");

    override val errorReason: ErrorReason
        get() = ErrorReason(httpStatus.value(), code, message)
}
