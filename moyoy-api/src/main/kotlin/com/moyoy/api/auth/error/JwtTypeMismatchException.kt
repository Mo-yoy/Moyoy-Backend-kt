package com.moyoy.api.auth.error

import com.moyoy.common.error.MoyoyException

class JwtTypeMismatchException : MoyoyException(AuthErrorCode.TOKEN_TYPE_MISMATCH)
