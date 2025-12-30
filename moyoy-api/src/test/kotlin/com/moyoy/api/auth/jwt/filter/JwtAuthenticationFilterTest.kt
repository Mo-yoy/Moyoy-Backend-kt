package com.moyoy.api.auth.jwt.filter

import com.moyoy.api.auth.error.InvalidJwtException
import com.moyoy.api.auth.jwt.JwtProvider
import com.moyoy.api.auth.jwt.JwtType
import com.moyoy.api.auth.jwt.dto.JwtUserClaims
import com.moyoy.api.auth.security.GithubOAuth2UserPrincipal
import com.moyoy.common.const.JwtConst
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {
    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    private val emptyFilterChain = FilterChain { _: ServletRequest, _: ServletResponse -> }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("헤더에 토큰이 아예 없으면 비인증 상태로 통과해야 한다.")
    fun no_token_pass_anonymous() {
        // given
        val request = MockHttpServletRequest("GET", "/api/v1/private")
        val response = MockHttpServletResponse()

        // when & then
        jwtAuthenticationFilter.doFilter(request, response, emptyFilterChain)

        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @DisplayName("유효한 Access Token을 넣으면 인증 객체가 생성되어야 한다")
    fun valid_token_authentication_success() {
        // given
        val userDto = JwtUserClaims(100L, "ROLE_USER")
        val token = jwtProvider.createJwtToken(userDto, JwtType.ACCESS)
        val request = MockHttpServletRequest("GET", "/api/v1/private")
        request.addHeader("Authorization", "Bearer $token")
        val response = MockHttpServletResponse()

        // when
        jwtAuthenticationFilter.doFilter(request, response, emptyFilterChain)

        // then
        val auth = SecurityContextHolder.getContext().authentication
        assertThat(auth).isNotNull

        val principal = auth!!.principal as GithubOAuth2UserPrincipal
        assertThat(principal.authorities)
            .extracting("authority")
            .contains("ROLE_USER")

        assertThat(principal.attributes[JwtConst.CLAIM_USER_ID]).isEqualTo(100L)
    }

    @Test
    @DisplayName("Bearer 접두사가 없는 잘못된 헤더는 InvalidJwtException을 던져야 한다")
    fun invalid_prefix_throw_exception() {
        // given
        val request = MockHttpServletRequest("GET", "/api/v1/private")
        request.addHeader("Authorization", "NoBearer TokenValue")
        val response = MockHttpServletResponse()

        // when & then
        assertThrows(InvalidJwtException::class.java) {
            jwtAuthenticationFilter.doFilter(request, response, emptyFilterChain)
        }
    }
}
