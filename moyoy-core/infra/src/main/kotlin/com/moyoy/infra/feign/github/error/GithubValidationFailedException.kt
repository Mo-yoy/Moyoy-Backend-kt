package com.moyoy.infra.feign.github.error

import com.moyoy.common.error.MoyoyException
import com.moyoy.infra.feign.github.error.code.GithubErrorCode.GITHUB_VALIDATION_FAILED

class GithubValidationFailedException : MoyoyException(GITHUB_VALIDATION_FAILED)
