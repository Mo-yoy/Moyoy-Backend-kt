package com.moyoy.api.auth.security

import com.moyoy.common.utils.CookieUtils
import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

class HttpCookieOAuth2AuthorizationRequestRepositoryTest {
    private lateinit var repository: HttpCookieOAuth2AuthorizationRequestRepository
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        repository = HttpCookieOAuth2AuthorizationRequestRepository()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    @Test
    @DisplayName("쿠키로부터 OAuth2AuthorizationRequest 인증 요청을 성공적으로 불러와야 한다")
    fun loadAuthorizationRequestTest() {
        // given
        val authRequest =
            OAuth2AuthorizationRequest
                .authorizationCode()
                .authorizationUri("https://test")
                .clientId("test-client-id")
                .build()

        // 실제 저장 로직을 이용해 OAuth2AuthorizationRequest 쿠키를 먼저 생성
        val serializedData = CookieUtils.serialize(authRequest)
        val cookie = Cookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serializedData)
        request.setCookies(cookie)

        // when
        val loadedRequest = repository.loadAuthorizationRequest(request)

        // then
        assertThat(loadedRequest).isNotNull
        assertThat(loadedRequest?.clientId).isEqualTo("test-client-id")
        assertThat(loadedRequest?.authorizationUri).isEqualTo("https://test")
    }

    @Test
    @DisplayName("요청에 OAuth2AuthorizationRequest 인증 요청 쿠키가 없으면 null을 반환해야 한다")
    fun loadAuthorizationRequest_null_when_no_cookie() {
        // given

        // when
        val result = repository.loadAuthorizationRequest(request)

        // then
        assertThat(result).isNull()
    }

    @Test
    @DisplayName("OAuth2AuthorizationRequest 인증 요청을 쿠키에 성공적으로 저장해야 한다")
    fun saveAuthorizationRequestTest() {
        // given
        val authRequest =
            OAuth2AuthorizationRequest
                .authorizationCode()
                .authorizationUri("https://test")
                .clientId("test-client-id")
                .build()

        // when
        repository.saveAuthorizationRequest(authRequest, request, response)

        // then
        val cookie = response.getCookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        val decodedRequest = CookieUtils.deserialize(cookie!!, OAuth2AuthorizationRequest::class.java)
        assertThat(decodedRequest.clientId).isEqualTo("test-client-id")
        assertThat(decodedRequest.authorizationUri).isEqualTo("https://test")
    }

    @Test
    @DisplayName("저장할 OAuth2AuthorizationRequest 인증 요청이 null 이면 혹시 남아있을 기존 쿠키를 삭제해야 한다")
    fun saveAuthorizationRequest_null_test() {
        // given
        request.setCookies(Cookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "bad-data"))

        // when
        repository.saveAuthorizationRequest(null, request, response)

        // then
        val cookie = response.getCookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        assertThat(cookie).isNotNull
        assertThat(cookie?.maxAge).isEqualTo(0)
    }

    @Test
    @DisplayName("인증 요청 삭제 시, 기존 데이터를 반환하고 쿠키는 만료시켜야 한다")
    fun removeAuthorizationRequestTest() {
        // given
        val authRequest =
            OAuth2AuthorizationRequest
                .authorizationCode()
                .authorizationUri("https://test")
                .clientId("test-client-id")
                .build()

        val serializedData = CookieUtils.serialize(authRequest)
        val cookie = Cookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serializedData)
        request.setCookies(cookie)

        // when
        val removedRequest = repository.removeAuthorizationRequest(request, response)

        // then
        assertThat(removedRequest).isNotNull
        assertThat(removedRequest?.clientId).isEqualTo("test-client-id")

        val responseCookie = response.getCookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        assertThat(responseCookie?.maxAge).isEqualTo(0)
    }
}
