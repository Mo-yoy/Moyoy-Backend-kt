package com.moyoy.api.auth.security

import com.moyoy.api.user.application.RegisterOrSyncUserUseCase
import com.moyoy.common.const.GithubAttributes
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * Spring Security 에서 요구하는 OAuth2User
 * 추후, Security Context Holder의 User Principal이 됨
 */

class GithubOAuth2UserPrincipal(
    private val authorities: Set<GrantedAuthority>,
    private val attributes: Map<String, Any>
) : OAuth2User {
    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getName(): String = attributes[GithubAttributes.ID].toString()

    val id: Long
        get() = attributes[GithubAttributes.ID] as Long

    companion object {
        fun from(result: RegisterOrSyncUserUseCase.Output): GithubOAuth2UserPrincipal {
            val authorities =
                setOf(
                    SimpleGrantedAuthority(result.role.value)
                )
            val attributes =
                mapOf(
                    GithubAttributes.ID to result.id
                )

            return GithubOAuth2UserPrincipal(authorities, attributes)
        }
    }
}
