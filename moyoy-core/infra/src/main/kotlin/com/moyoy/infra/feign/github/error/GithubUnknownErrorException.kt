package com.moyoy.infra.feign.github.error

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import com.moyoy.common.error.MoyoyException

class GithubUnknownErrorException(
    status: Int
) : MoyoyException(
        object : BaseErrorCode {
            override val errorReason = ErrorReason(status, "GITHUB_UNKNOWN_ERROR", "GitHub UNKNOWN Error (status=$status)")
        }
    )
