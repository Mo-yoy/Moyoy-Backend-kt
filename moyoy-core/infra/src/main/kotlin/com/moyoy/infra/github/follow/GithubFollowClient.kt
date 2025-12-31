package com.moyoy.infra.github.follow

import com.moyoy.common.const.GithubAttributes.USER_TYPE
import com.moyoy.domain.follow.GithubFollowUserProfile
import com.moyoy.infra.github.GithubPaginationApiTemplate
import com.moyoy.infra.github.follow.dto.GithubFollowUserInfoResponse
import com.moyoy.infra.github.user.GithubUserApi
import org.springframework.stereotype.Component

@Component
class GithubFollowClient private constructor(
    private val githubFollowApi: GithubFollowApi,
    private val githubUserApi: GithubUserApi,
    private val paginationApiTemplate: GithubPaginationApiTemplate
) {
    companion object {
        private const val PER_PAGE = 100
    }

    fun fetchAllFollowings(bearerToken: String): List<GithubFollowUserProfile> {
        return paginationApiTemplate
            .fetchAll(
                perPage = PER_PAGE,
                fetcher = { page -> githubFollowApi.fetchPagedFollowings(bearerToken, PER_PAGE, page) }
            ).filter(::isUserType)
            .map(::convert)
    }

    fun fetchAllFollowers(bearerToken: String): List<GithubFollowUserProfile> {
        return paginationApiTemplate
            .fetchAll(
                perPage = PER_PAGE,
                fetcher = { page -> githubFollowApi.fetchPagedFollowers(bearerToken, PER_PAGE, page) }
            ).filter(::isUserType)
            .map(::convert)
    }

    // / GithubFollow API는 username을 매개변수로 하지만, 이는 변경 가능함
    fun follow(
        bearerToken: String,
        targetGithubId: Int
    ) {
        val latestUserName = getLatestLoginName(bearerToken, targetGithubId)
        githubFollowApi.follow(bearerToken, latestUserName)
    }

    fun unfollow(
        bearerToken: String,
        targetGithubId: Int
    ) {
        val latestUserName = getLatestLoginName(bearerToken, targetGithubId)
        githubFollowApi.unfollow(bearerToken, latestUserName)
    }

    private fun isUserType(account: GithubFollowUserInfoResponse): Boolean {
        return account.type.equals(USER_TYPE, ignoreCase = true)
    }

    private fun convert(response: GithubFollowUserInfoResponse): GithubFollowUserProfile {
        return GithubFollowUserProfile(
            githubId = response.id,
            username = response.login,
            profileImgUrl = response.avatarUrl
        )
    }

    private fun getLatestLoginName(
        token: String,
        githubId: Int
    ): String {
        val response = githubUserApi.fetchUserInfo(token, githubId)
        return response.body!!.login
    }
}
