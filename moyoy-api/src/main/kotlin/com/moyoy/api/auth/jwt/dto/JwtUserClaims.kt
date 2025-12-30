package com.moyoy.api.auth.jwt.dto

import com.moyoy.api.auth.security.GithubOAuth2UserPrincipal
import org.springframework.security.core.Authentication

data class JwtUserClaims(
    val userId: Long,
    val authority: String
) {
    companion object {
        fun from(authentication: Authentication): JwtUserClaims {
            val githubUser = authentication.principal as GithubOAuth2UserPrincipal

            return JwtUserClaims(
                githubUser.id,
                githubUser.authorities.toString()
            )
        }
    }
}
