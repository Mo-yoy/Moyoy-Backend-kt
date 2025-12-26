package com.moyoy.common.error

open class MoyoyException(
    val errorCode: BaseErrorCode
) : RuntimeException(errorCode.errorReason.errorMessage) {
    val errorReason: ErrorReason
        get() = errorCode.errorReason
}
