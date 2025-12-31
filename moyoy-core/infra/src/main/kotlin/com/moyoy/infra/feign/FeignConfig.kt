package com.moyoy.infra.feign

import com.moyoy.infra.MoyoyConfig
import com.moyoy.infra.feign.github.GithubPaginationApiTemplate
import com.moyoy.infra.feign.github.follow.GithubFollowApi
import com.moyoy.infra.feign.github.follow.GithubFollowClient
import com.moyoy.infra.feign.github.user.GithubUserApi
import com.moyoy.infra.feign.github.user.GithubUserClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean

@EnableFeignClients
class FeignConfig : MoyoyConfig {
    @Bean
    fun githubUserClient(githubUserApi: GithubUserApi): GithubUserClient {
        return GithubUserClient(githubUserApi)
    }

    @Bean
    fun githubFollowClient(
        githubFollowApi: GithubFollowApi,
        githubUserApi: GithubUserApi,
        githubPaginationApiTemplate: GithubPaginationApiTemplate
    ): GithubFollowClient {
        return GithubFollowClient(githubFollowApi, githubUserApi, githubPaginationApiTemplate)
    }

    @Bean
    fun githubPaginationApiTemplate(): GithubPaginationApiTemplate {
        return GithubPaginationApiTemplate()
    }
}
