package com.moyoy.domain.user.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.domain.user.error.code.UserErrorCode

class UserGithubAccountTypeNotAllowException :
    MoyoyException(
        UserErrorCode.NOT_ALLOWED_GITHUB_ACCOUNT_TYPE
    )
