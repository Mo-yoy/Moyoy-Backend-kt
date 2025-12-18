package com.moyoy.api.auth.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {
    @Bean
    fun moyoySecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .cors { }
            .csrf { it.disable() }
            .sessionManagement { it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
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
            }
//            .oauth2Login { oauth2 ->
//                oauth2
//                    .authorizationEndpoint { authorization ->
//                        authorization
//                            .baseUri("/auth/login")
//                            .authorizationRequestRepository(authorizationRequestRepository)
//                    }.userInfoEndpoint { userInfo ->
//                        userInfo.userService(oAuth2UserService)
//                    }.authorizedClientService(authorizedClientService)
//                    .successHandler(successHandler)
//                    .failureHandler(failureHandler)
//            }.exceptionHandling { exception ->
//                exception
//                    .authenticationEntryPoint(authenticationEntryPoint)
//                    .accessDeniedHandler(accessDeniedHandler)
//            }

        return http.build()
    }
}
