package com.moyoy.infra.feign.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.feign.github.error.code.GithubErrorCode.GITHUB_UNAUTHORIZED

class GithubUnauthorizedException :
    MoyoyException(
        GITHUB_UNAUTHORIZED
    )
