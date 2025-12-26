package com.moyoy.api.auth.error

import com.moyoy.common.error.MoyoyException

class BlockedJwtException : MoyoyException(AuthErrorCode.BLOCKED_TOKEN)
