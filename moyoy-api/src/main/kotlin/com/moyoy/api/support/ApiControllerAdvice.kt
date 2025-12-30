package com.moyoy.api.support

import com.moyoy.api.support.response.ApiResponse
import com.moyoy.common.error.MoyoyException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(MoyoyException::class)
    fun handleMoyoyException(ex: MoyoyException): ResponseEntity<ApiResponse<Nothing?>> {
        val errorReason = ex.errorReason

        return ResponseEntity
            .status(errorReason.status)
            .body(ApiResponse.fail(errorReason))
    }
}
