package com.moyoy.api.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.moyoy.api.support.response.ApiResponse
import com.moyoy.common.error.BaseErrorCode
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

/**
 * ControllerAdvice가 아닌 곳(Filter 등)에서 에러를 직접 처리할 때 사용
 */
@Component
class ErrorResponseHandler(
    private val objectMapper: ObjectMapper
) {
    fun writeErrorResponse(
        response: HttpServletResponse,
        httpStatusCode: Int,
        errorCode: BaseErrorCode
    ) {
        response.apply {
            status = httpStatusCode
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name()
        }

        val apiResponse = ApiResponse.fail(errorCode.errorReason)
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)

        response.writer.write(jsonResponse)
    }
}
