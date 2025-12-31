package com.moyoy.infra.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.github.error.code.GithubErrorCode.GITHUB_VALIDATION_FAILED

class GithubValidationFailedException : MoyoyException(GITHUB_VALIDATION_FAILED)
