package com.moyoy.api.support.response

import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK

class ApiResponse<T> private constructor(
    val status: Int,
    val code: String,
    val message: String?,
    val data: T?
) {
    companion object {
        fun <S> success(data: S): ApiResponse<S> {
            return ApiResponse(OK.value(), OK.reasonPhrase, null, data)
        }

        fun accepted(): ApiResponse<Nothing?> {
            return ApiResponse(ACCEPTED.value(), ACCEPTED.reasonPhrase, null, null)
        }

        fun noContent(): ApiResponse<Nothing?> {
            return ApiResponse(NO_CONTENT.value(), NO_CONTENT.reasonPhrase, null, null)
        }
    }
}
