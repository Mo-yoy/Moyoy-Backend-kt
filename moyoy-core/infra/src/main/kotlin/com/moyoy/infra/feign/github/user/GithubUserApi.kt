package com.moyoy.infra.feign.github.user

import com.moyoy.infra.feign.github.GithubFeignConfig
import com.moyoy.infra.feign.github.user.dto.GithubUserInfoResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "githubProfileClient",
    url = "https://api.github.com",
    configuration = [GithubFeignConfig::class]
)
interface GithubUserApi {
    /**
     * [Get a user using their ID](https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-a-user-using-their-id)
     *
     * GitHub 계정의 **numeric ID(account_id)**를 사용해 해당 사용자의 공개 정보를 조회합니다.
     * (로그인 ID는 변경될 수 있지만, numeric ID는 변하지 않는 durable identifier)
     *
     * Enterprise Managed User 또는 GitHub App bot 계정 정보에 접근하려면,
     * 해당 조직에 접근 권한이 있는 사용자 또는 GitHub App 인증이 필요합니다.
     * 권한이 없으면 **404 Not Found**가 반환됩니다.
     *
     * **Note:** 응답의 `email` 필드는 사용자가 프로필에서 공개 설정한 이메일만 노출됩니다.
     * 비공개 이메일은 null 로 반환되며, 본인의 전체 이메일 목록은 [Emails API](https://docs.github.com/en/rest/users/emails)를 통해 확인할 수 있습니다.
     *
     * **Response status codes**
     * - 200 OK: 요청 성공, 사용자 정보 반환
     * - 404 Not Found: 리소스를 찾을 수 없음 (권한 없음 또는 계정 없음)
     *
     * @param bearerToken GitHub OAuth Access Token (형식: "Bearer {token}")
     * @param githubUserId GitHub 고유 숫자 ID (numeric ID / account_id)
     * @return [GithubUserInfoResponse]와 API 응답 헤더(X-RateLimit 등)를 포함한 [ResponseEntity]
     */
    @GetMapping("/user/{userId}")
    fun fetchUserInfo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) bearerToken: String,
        @PathVariable("userId") githubUserId: Int
    ): ResponseEntity<GithubUserInfoResponse>
}
