package com.moyoy.infra.github.follow

import com.moyoy.infra.github.GithubFeignConfig
import com.moyoy.infra.github.follow.dto.GithubFollowUserInfoResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "githubFollowClient",
    url = "https://api.github.com",
    configuration = [GithubFeignConfig::class]
)
internal interface GithubFollowApi {
    /**
     * [List followers of the authenticated user](https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#list-followers-of-the-authenticated-user)
     *
     * 인증된 사용자의 팔로워 목록을 조회합니다.
     *
     * **Response status codes**
     * - 200 OK: 요청 성공, 팔로워 목록 반환
     * - 304 Not Modified: 변경 없음 (캐시 사용 시)
     * - 401 Unauthorized: 인증 실패, 액세스 토큰 누락 또는 잘못됨
     * - 403 Forbidden: 요청 거부됨 (API 제한 등)
     *
     * @param bearerToken GitHub OAuth Access Token ("Bearer {token}")
     * @param perPage 한 페이지당 결과 수 (기본 30, 최대 100)
     * @param page 페이지 번호 (1부터 시작)
     * @return GitHub 사용자 목록을 담은 [ResponseEntity]
     */
    @GetMapping("/user/followers")
    fun fetchPagedFollowers(
        @RequestHeader(AUTHORIZATION) bearerToken: String,
        @RequestParam("per_page") perPage: Int,
        @RequestParam("page") page: Int
    ): ResponseEntity<List<GithubFollowUserInfoResponse>>

    /**
     * [List the people the authenticated user follows](https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#list-the-people-the-authenticated-user-follows)
     *
     * 인증된 사용자가 팔로우하는 사용자 목록을 조회합니다.
     *
     * **Response status codes**
     * - 200 OK: 요청 성공, 팔로우 중인 사용자 목록 반환
     * - 304 Not Modified: 변경 없음 (캐시 사용 시)
     * - 401 Unauthorized: 인증 실패, 액세스 토큰 누락 또는 잘못됨
     * - 403 Forbidden: 요청 거부됨 (API 제한 등)
     *
     * @param bearerToken GitHub OAuth Access Token ("Bearer {token}")
     * @param perPage 한 페이지당 결과 수 (기본 30, 최대 100)
     * @param page 페이지 번호 (1부터 시작)
     * @return GitHub 사용자 목록을 담은 [ResponseEntity]
     */
    @GetMapping("/user/following")
    fun fetchPagedFollowings(
        @RequestHeader(AUTHORIZATION) bearerToken: String,
        @RequestParam("per_page") perPage: Int,
        @RequestParam("page") page: Int
    ): ResponseEntity<List<GithubFollowUserInfoResponse>>

    /**
     * [Follow a user](https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#follow-a-user)
     *
     * 인증된 사용자가 지정한 유저를 팔로우합니다.
     *
     * **Response status codes**
     * - 204 No Content: 요청 성공, 팔로우 완료
     * - 304 Not Modified: 이미 해당 유저를 팔로우 중
     * - 401 Unauthorized: 인증 실패, 액세스 토큰 누락 또는 잘못됨
     * - 403 Forbidden: 요청 거부됨 (API 제한 등)
     * - 404 Not Found: 대상 username이 존재하지 않음
     * - 422 Unprocessable Entity: 검증 실패, 또는 과도한 요청으로 스팸 처리됨
     *
     * @param bearerToken GitHub OAuth Access Token ("Bearer {token}")
     * @param username 팔로우할 대상 GitHub username
     * @return 요청 결과를 담은 [ResponseEntity]
     */
    @PutMapping("/user/following/{username}")
    fun follow(
        @RequestHeader(AUTHORIZATION) bearerToken: String,
        @PathVariable("username") username: String
    ): ResponseEntity<Unit>

    /**
     * [Unfollow a user](https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#unfollow-a-user)
     *
     * 인증된 사용자가 지정한 유저를 언팔로우합니다.
     *
     * **Response status codes**
     * - 204 No Content: 요청 성공, 언팔로우 완료
     * - 304 Not Modified: 이미 언팔로우 상태
     * - 401 Unauthorized: 인증 실패, 액세스 토큰 누락 또는 잘못됨
     * - 403 Forbidden: 요청 거부됨 (API 제한 등)
     * - 404 Not Found: 대상 username이 존재하지 않음
     *
     * @param bearerToken GitHub OAuth Access Token ("Bearer {token}")
     * @param username 언팔로우할 대상 GitHub username
     * @return 요청 결과를 담은 [ResponseEntity]
     */
    @DeleteMapping("/user/following/{username}")
    fun unfollow(
        @RequestHeader(AUTHORIZATION) bearerToken: String,
        @PathVariable("username") username: String
    ): ResponseEntity<Unit>
}
