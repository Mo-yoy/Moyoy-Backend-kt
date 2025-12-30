package com.moyoy.common.utils

import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.io.NotSerializableException
import kotlin.test.Test

class CookieUtilsTest {
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    @Nested
    @DisplayName("findCookie를 호출할 경우 ")
    inner class FindCookieTest {
        @Test
        @DisplayName("이름이 일치하는 쿠기가 존재하는 경우, 이를 정확히 찾아야 한다")
        fun should_ReturnCookie_When_Exists() {
            // given
            request.setCookies(Cookie("my_cookie", "my_value"))

            // when
            val found = CookieUtils.findCookie(request, "my_cookie")

            // then
            Assertions.assertThat(found).isNotNull
            Assertions.assertThat(found?.value).isEqualTo("my_value")
        }

        @Test
        @DisplayName("쿠키 리스트 자체가 비어있으면(null) null을 반환해야 한다")
        fun should_ReturnNull_When_CookiesArrayIsNull() {
            // given

            // when
            val found = CookieUtils.findCookie(request, "any_name")

            // then
            Assertions.assertThat(found).isNull()
        }

        @Test
        @DisplayName("쿠키는 존재하지만 이름이 일치하는 것이 없으면 null을 반환해야 한다")
        fun should_ReturnNull_When_NoMatchingName() {
            // given
            request.setCookies(Cookie("other_cookie", "other_value"))

            // when
            val found = CookieUtils.findCookie(request, "target_cookie")

            // then
            Assertions.assertThat(found).isNull()
        }
    }

    @Test
    @DisplayName("addCookie 호출 시, 모든 보안 속성이 포함된 쿠키가 Set-Cookie 헤더에 추가되어야 한다")
    fun should_AddCookieWithSecurityAttributes() {
        // given
        val name = "test-cookie"
        val value = "test-value"

        // when
        CookieUtils.addCookie(response, name, value)

        // then
        val setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE)

        Assertions.assertThat(setCookieHeader).isNotNull
        Assertions.assertThat(setCookieHeader).contains("$name=$value")
        Assertions.assertThat(setCookieHeader).contains("Path=/")
        Assertions.assertThat(setCookieHeader).contains("HttpOnly")
        Assertions.assertThat(setCookieHeader).contains("Max-Age=1800")
        Assertions.assertThat(setCookieHeader).contains("SameSite=None")
        Assertions.assertThat(setCookieHeader).contains("Secure")
    }

    @Nested
    @DisplayName("deleteCookie를 호출 시, ")
    inner class DeleteCookieTest {
        @Test
        @DisplayName("삭제하려는 이름의 쿠키가 존재하면 만료 설정(maxAge=0)을 응답에 추가한다")
        fun should_AddExpiredCookie_When_Exists() {
            // given
            val cookieName = "delete_target"
            request.setCookies(Cookie(cookieName, "old_value"))

            // when
            CookieUtils.deleteCookie(request, response, cookieName)

            // then
            val deletedCookie = response.getCookie(cookieName)
            Assertions.assertThat(deletedCookie).isNotNull
            Assertions.assertThat(deletedCookie?.maxAge).isEqualTo(0)
            Assertions.assertThat(deletedCookie?.value).isEmpty()
        }

        @Test
        @DisplayName("삭제하려는 이름의 쿠키가 없으면 응답에 아무런 조치도 취하지 않는다")
        fun should_DoNothing_When_NotExists() {
            // given
            request.setCookies(Cookie("other_cookie", "other_value"))

            // when
            CookieUtils.deleteCookie(request, response, "non_existent")

            // then
            val resultCookie = response.getCookie("non_existent")
            Assertions.assertThat(resultCookie).isNull()
        }

        @Test
        @DisplayName("요청에 쿠키가 하나도 없어도(null) 에러 없이 종료되어야 한다")
        fun should_HandleNullCookiesGracefully() {
            // given: request.cookies is null

            // when & then
            CookieUtils.deleteCookie(request, response, "any_name")
            // No exception occurs
            Assertions.assertThat(response.cookies).isEmpty()
        }
    }

    @Nested
    @DisplayName("serialize를 호출할 경우, ")
    inner class SerializeTest {
        @Test
        @DisplayName("직렬화 가능한 객체를 전달하면 Base64 문자열을 반환한다")
        fun should_ReturnBase64String_When_SerializableObject() {
            // given
            val original = "test"

            // when
            val result = CookieUtils.serialize(original)

            // then
            Assertions.assertThat(result).isNotEmpty()
            Assertions.assertThat(result).isNotBlank()
            Assertions.assertThat(result).doesNotContain("+", "/")
        }

        @Test
        @DisplayName("직렬화 불가능한 객체를 전달하면 예외가 발생한다")
        fun should_ThrowException_When_NotSerializable() {
            // given
            class NonSerializableClass(
                val name: String
            )
            val obj = NonSerializableClass("test")

            // when & then
            assertThrows<NotSerializableException> {
                CookieUtils.serialize(obj)
            }
        }
    }

    @Nested
    @DisplayName("deserialize를 호출할 경우, ")
    inner class DeserializeTest {
        @Test
        @DisplayName("올바른 쿠키를 전달하면 원래 객체로 복구되어야 한다")
        fun should_RestoreObject_When_ValidCookie() {
            // given
            val original = "Test String Data"
            val serialized = CookieUtils.serialize(original)
            val cookie = Cookie("test", serialized)

            // when
            val result = CookieUtils.deserialize(cookie, String::class.java)

            // then
            Assertions.assertThat(result).isEqualTo(original)
        }

        @Test
        @DisplayName("형식이 잘못된 쿠키 값을 전달하면 예외가 발생한다")
        fun should_ThrowException_When_InvalidBase64() {
            // given
            val cookie = Cookie("test", "invalid-base64-value-!")

            // when & then
            assertThrows<Exception> {
                CookieUtils.deserialize(cookie, Any::class.java)
            }
        }

        @Test
        @DisplayName("다른 클래스 타입으로 역직렬화를 시도하면 할당 시점에 예외가 발생한다")
        fun should_ThrowException_When_WrongClassType() {
            // given
            val serialized = CookieUtils.serialize("I am String")
            val cookie = Cookie("test", serialized)

            // when & then
            assertThrows<ClassCastException> {
                val result: Long = CookieUtils.deserialize(cookie, Long::class.javaObjectType)
            }
        }
    }
}
