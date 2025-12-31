package com.moyoy.infra.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.github.error.code.GithubErrorCode.GITHUB_RESOURCE_NOT_FOUND

class GithubResourceNotFoundException :
    MoyoyException(
        GITHUB_RESOURCE_NOT_FOUND
    )
