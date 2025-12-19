package com.moyoy.api.auth.error

import com.moyoy.common.error.MoyoyException

class ExpiredJwtException : MoyoyException(AuthErrorCode.EXPIRED_TOKEN)
