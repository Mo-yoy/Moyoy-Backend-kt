package com.moyoy.api.auth.security.config

import com.moyoy.api.auth.jwt.filter.JwtAuthenticationFilter
import com.moyoy.api.auth.security.CustomAccessDeniedHandler
import com.moyoy.api.auth.security.CustomAuthenticationEntryPoint
import com.moyoy.api.auth.security.CustomAuthenticationFailureHandler
import com.moyoy.api.auth.security.CustomAuthenticationSuccessHandler
import com.moyoy.api.auth.security.CustomOAuth2UserService
import com.moyoy.api.auth.security.HttpCookieOAuth2AuthorizationRequestRepository
import com.moyoy.api.auth.security.RdbOAuth2AuthorizedClientService
import com.moyoy.domain.user.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtExceptionHanDleFilter: JwtAuthenticationFilter,
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
    private val oAuth2UserService: CustomOAuth2UserService,
    private val authorizedClientService: RdbOAuth2AuthorizedClientService,
    private val successHandler: CustomAuthenticationSuccessHandler,
    private val failureHandler: CustomAuthenticationFailureHandler,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler
) {
    @Bean
    fun moyoySecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .cors { }
            .csrf { it.disable() }
            .sessionManagement { it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter::class.java)
            .addFilterBefore(jwtExceptionHanDleFilter, JwtAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/health", "/")
                    .permitAll()
                    .requestMatchers("/error/**", "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/api/v1/auth/reissue/token")
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/static/swagger-ui/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2Login { oauth2 ->
                oauth2
                    .authorizationEndpoint { authorization ->
                        authorization
                            .baseUri("/auth/login")
                            .authorizationRequestRepository(authorizationRequestRepository)
                    }.userInfoEndpoint { userInfo ->
                        userInfo.userService(oAuth2UserService)
                    }.authorizedClientService(authorizedClientService)
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
            }.exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }

        return http.build()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val hierarchy =
            """
            ${Role.ADMIN.value} > ${Role.USER.value}
            ${Role.USER.value} > ${Role.ANONYMOUS.value}
            """.trimIndent()

        return fromHierarchy(hierarchy)
    }
}
