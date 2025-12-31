package com.moyoy.infra.github.user

import com.moyoy.infra.github.user.dto.GithubUserInfoResponse
import org.springframework.stereotype.Component

@Component
class GithubUserClient private constructor(
    private val githubUserApi: GithubUserApi
) {
    fun fetchUserInfo(
        bearerToken: String,
        githubUserId: Int
    ): GithubUserInfoResponse {
        return githubUserApi.fetchUserInfo(bearerToken, githubUserId).body!!
    }
}
