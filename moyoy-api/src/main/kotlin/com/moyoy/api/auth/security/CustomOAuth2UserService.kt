package com.moyoy.api.auth.security

import com.moyoy.api.user.application.RegisterUserOrSyncUsecase
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
    private val registerUserOrSyncUsecase: RegisterUserOrSyncUsecase
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        val input = RegisterUserOrSyncUsecase.Input.from(oAuth2User)
        val output = registerUserOrSyncUsecase.execute(input)

        return GithubOAuth2UserPrincipal.from(output)
    }
}
