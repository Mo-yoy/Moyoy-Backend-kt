package com.moyoy.infra.feign.github

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE

class GithubFeignConfig {
    @Bean
    fun githubRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate ->
            requestTemplate.header(CONTENT_TYPE, "application/json")
            requestTemplate.header(ACCEPT, "application/vnd.github+json")
            requestTemplate.header("X-GitHub-Api-Version", "2022-11-28")
        }
    }

    @Bean
    fun githubErrorDecoder(): GithubErrorDecoder {
        return GithubErrorDecoder()
    }
}
