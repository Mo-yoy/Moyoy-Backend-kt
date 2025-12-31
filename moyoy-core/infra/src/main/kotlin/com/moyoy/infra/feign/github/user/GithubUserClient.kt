package com.moyoy.infra.feign.github.user

import com.moyoy.infra.feign.github.user.dto.GithubUserInfoResponse

class GithubUserClient(
    private val githubUserApi: GithubUserApi
) {
    fun fetchUserInfo(
        bearerToken: String,
        githubUserId: Int
    ): GithubUserInfoResponse {
        return githubUserApi.fetchUserInfo(bearerToken, githubUserId).body!!
    }
}
