package com.moyoy.api.auth.jwt

import com.moyoy.api.auth.security.GithubOAuth2UserPrincipal
import org.springframework.security.core.Authentication

data class JwtUserDto(
    val userId: Long,
    val authority: String
) {
    companion object {
        fun from(authentication: Authentication): JwtUserDto {
            val githubUser = authentication.principal as GithubOAuth2UserPrincipal

            return JwtUserDto(
                githubUser.id,
                githubUser.authorities.toString()
            )
        }
    }
}
