package com.moyoy.api.auth.error

import com.moyoy.common.error.MoyoyException

class InvalidJwtException : MoyoyException(AuthErrorCode.INVALID_TOKEN)
