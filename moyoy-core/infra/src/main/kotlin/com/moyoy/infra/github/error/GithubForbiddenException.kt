package com.moyoy.infra.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.github.error.code.GithubErrorCode.GITHUB_FORBIDDEN

// 권한 부족 Or API Limit 초과
class GithubForbiddenException :
    MoyoyException(
        GITHUB_FORBIDDEN
    )
