package com.moyoy.infra.feign.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.feign.github.error.code.GithubErrorCode.GITHUB_SERVER_UNAVAILABLE

class GithubServerUnavailableException :
    MoyoyException(
        GITHUB_SERVER_UNAVAILABLE
    )
