package com.moyoy.infra.feign.github.error

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.feign.github.error.code.GithubErrorCode.GITHUB_CLIENT_ERROR

class GithubClientErrorException(
    status: Int
) : MoyoyException(
        object : BaseErrorCode {
            override val errorReason: ErrorReason =
                ErrorReason(
                    status = status,
                    code = GITHUB_CLIENT_ERROR.code,
                    errorMessage = "${GITHUB_CLIENT_ERROR.message} (, status = $status)"
                )
        }
    )
