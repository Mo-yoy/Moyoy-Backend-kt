package com.moyoy.common.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Base64

object CookieUtils {
    fun findCookie(
        request: HttpServletRequest,
        name: String
    ): Cookie? {
        return request.cookies?.find { it.name == name }
    }

    fun addCookie(
        response: HttpServletResponse,
        name: String,
        value: String
    ) {
        val cookie =
            ResponseCookie
                .from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(1800)
                .sameSite("None") // / TODO 로컬 환경, 테스트 환경 결정 후 환경 변수화 필요
                .secure(true)
                .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    fun deleteCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
        name: String
    ) {
        request.cookies?.forEach { cookie ->
            if (cookie.name == name) {
                cookie.value = ""
                cookie.path = "/"
                cookie.maxAge = 0
                response.addCookie(cookie)
            }
        }
    }

    fun serialize(obj: Any): String {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(obj) }
        return Base64.getUrlEncoder().encodeToString(baos.toByteArray())
    }

    fun <T> deserialize(
        cookie: Cookie,
        cls: Class<T>
    ): T {
        val data = Base64.getUrlDecoder().decode(cookie.value)
        ObjectInputStream(ByteArrayInputStream(data)).use { ois ->
            @Suppress("UNCHECKED_CAST")
            return ois.readObject() as T
        }
    }
}
