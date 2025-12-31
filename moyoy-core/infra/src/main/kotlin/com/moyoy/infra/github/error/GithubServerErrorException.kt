package com.moyoy.infra.github.error

import com.moyoy.common.error.BaseErrorCode
import com.moyoy.common.error.ErrorReason
import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.github.error.code.GithubErrorCode.GITHUB_SERVER_ERROR

class GithubServerErrorException(
    status: Int
) : MoyoyException(
        object : BaseErrorCode {
            override val errorReason: ErrorReason =
                ErrorReason(
                    status = status,
                    code = GITHUB_SERVER_ERROR.code,
                    errorMessage = "${GITHUB_SERVER_ERROR.message} (, status = $status)"
                )
        }
    )
