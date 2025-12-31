package com.moyoy.infra.github.error.code

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import org.springframework.http.HttpStatus

enum class GithubErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) : BaseErrorCode {
    GITHUB_CLIENT_ERROR(
        HttpStatus.BAD_REQUEST,
        "GITHUB_4XX",
        "GitHub 요청 중 알 수 없는 클라이언트 오류가 발생했습니다."
    ),

    GITHUB_LIMIT_PRE_CHECK_EXCEED(
        HttpStatus.BAD_REQUEST,
        "GITHUB_400_1",
        "요청이 GitHub API 한도를 초과할 것으로 예상됩니다. 1시간 뒤 다시 시도해 주세요."
    ),

    GITHUB_UNAUTHORIZED(
        HttpStatus.UNAUTHORIZED,
        "GITHUB_401_1",
        "GitHub 인증에 실패했습니다. Access Token을 확인해 주세요."
    ),

    GITHUB_FORBIDDEN(
        HttpStatus.FORBIDDEN,
        "GITHUB_403_1",
        "권한이 부족하거나 GitHub API Rate Limit이 초과되었습니다."
    ),

    GITHUB_RESOURCE_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "GITHUB_404_1",
        "존재하지 않는 GitHub 리소스입니다."
    ),

    GITHUB_VALIDATION_FAILED(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "GITHUB_422_1",
        "GitHub 요청 파라미터 검증에 실패했거나 과한 요청으로 스팸처리되었습니다."
    ),

    GITHUB_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "GITHUB_5XX",
        "GitHub 서버에서 알 수 없는 내부 오류가 발생했습니다."
    ),

    GITHUB_SERVER_UNAVAILABLE(
        HttpStatus.SERVICE_UNAVAILABLE,
        "GITHUB_503_1",
        "깃허브 서버가 현재 응답하지 않거나 연결에 문제가 있습니다."
    );

    override val errorReason: ErrorReason
        get() = ErrorReason(httpStatus.value(), code, message)
}
