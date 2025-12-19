package com.moyoy.api.auth.jwt.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.moyoy.api.support.response.ApiResponse
import com.moyoy.common.error.MoyoyException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

@Component
class JwtExceptionHandleFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (ex: MoyoyException) {
            setErrorResponse(ex, response)
        }
    }

    private fun setErrorResponse(
        ex: MoyoyException,
        response: HttpServletResponse
    ) {
        val errorReason = ex.errorReason

        response.status = errorReason.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(errorReason))
        response.writer.write(jsonResponse)
    }
}
