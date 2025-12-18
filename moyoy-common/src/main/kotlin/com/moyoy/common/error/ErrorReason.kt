package com.moyoy.common.error

data class ErrorReason(
    val status: Int,
    val code: String,
    var errorMessage: String
) {
    fun addDetailErrorMessage(detailMessage: String) {
        errorMessage += " $detailMessage"
    }
}
