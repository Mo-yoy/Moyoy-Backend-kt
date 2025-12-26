package com.moyoy.api.auth.security

import com.moyoy.api.user.application.RegisterOrSyncUserUseCase
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

/**
 * 우리 서비스는 Github 특화 서비스이므로 확장성보다는 Github 와의 밀결합을 선택.
 * 추후 다른 OAuth가 추가될 가능성이 매우 낮고, 리소스 부족으로 트레이드 오프함.
 */

@Component
class CustomOAuth2UserService(
    private val registerOrSyncUserUseCase: RegisterOrSyncUserUseCase
) : DefaultOAuth2UserService() {
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val githubUser = fetchGithubUser(oAuth2UserRequest)

        val input = RegisterOrSyncUserUseCase.Input.from(githubUser)
        val output = registerOrSyncUserUseCase.execute(input)

        return convertToUserPrincipal(output)
    }

    private fun fetchGithubUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        return super.loadUser(oAuth2UserRequest)
    }

    private fun convertToUserPrincipal(output: RegisterOrSyncUserUseCase.Output): GithubOAuth2UserPrincipal {
        return GithubOAuth2UserPrincipal.from(output)
    }
}
