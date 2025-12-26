package com.moyoy.api.auth.error

import com.moyoy.common.error.MoyoyException

class JwtNotExistException : MoyoyException(AuthErrorCode.TOKEN_NOT_EXIST)
