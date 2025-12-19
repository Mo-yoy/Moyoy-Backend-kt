package com.moyoy.api.auth.jwt.filter

import com.moyoy.api.auth.error.InvalidJwtException
import com.moyoy.api.auth.jwt.JwtPayloadExtractor
import com.moyoy.api.auth.jwt.JwtType
import com.moyoy.api.auth.jwt.JwtValidator
import com.moyoy.api.auth.security.GithubOAuth2UserPrincipal
import com.moyoy.common.const.GithubAttributes
import com.moyoy.common.const.JwtConst
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtValidator: JwtValidator,
    private val jwtPayloadExtractor: JwtPayloadExtractor
) : OncePerRequestFilter() {
    companion object {
        private const val GITHUB_LOGIN_REDIRECT_URL = "/auth/login/github"
        private const val GITHUB_LOGIN_AUTHORIZATION_CODE_URL = "/login/oauth2/code/github"
        private const val TOKEN_REISSUE_URL = "/api/v1/auth/reissue/token"
        private const val BEARER_PREFIX = "Bearer "

        private val whiteList =
            mapOf(
                GET.name() to
                    setOf(
                        GITHUB_LOGIN_REDIRECT_URL,
                        GITHUB_LOGIN_AUTHORIZATION_CODE_URL
                    ),
                POST.name() to
                    setOf(
                        TOKEN_REISSUE_URL
                    )
            )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val method = request.method
        val requestURI = request.requestURI

        if (whiteList[method]?.contains(requestURI) == true) {
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader = request.getHeader(AUTHORIZATION)

        // Access Token 이 null 인경우, 비인증 사용자로 간주
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response)
            return
        }

        val accessToken = resolveBearerToken(authorizationHeader)
        authenticate(accessToken)

        filterChain.doFilter(request, response)
    }

    private fun resolveBearerToken(header: String): String {
        if (!header.startsWith(BEARER_PREFIX)) {
            throw InvalidJwtException()
        }
        return header.substring(BEARER_PREFIX.length)
    }

    private fun authenticate(accessToken: String) {
        jwtValidator.validate(JwtType.ACCESS, accessToken)

        val info = jwtPayloadExtractor.extractUserInfo(accessToken)

        val authorities = setOf(SimpleGrantedAuthority(info.authority))
        val attributes = mapOf<String, Any>(JwtConst.CLAIM_USER_ID to info.userId)

        val principal = GithubOAuth2UserPrincipal(authorities, attributes)
        val authentication =
            OAuth2AuthenticationToken(
                principal,
                principal.authorities,
                GithubAttributes.GITHUB
            )
        SecurityContextHolder.getContext().authentication = authentication
    }
}
