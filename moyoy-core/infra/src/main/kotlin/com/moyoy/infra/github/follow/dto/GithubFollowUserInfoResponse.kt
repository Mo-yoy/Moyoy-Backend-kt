package com.moyoy.infra.github.follow.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * GitHub 팔로우(팔로워/팔로잉) 목록 조회 시 반환되는 사용자 정보 응답 객체입니다.
 */
data class GithubFollowUserInfoResponse(
    val login: String,
    val id: Int,
    @JsonProperty("node_id")
    val nodeId: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    @JsonProperty("gravatar_id")
    val gravatarId: String?,
    val url: String,
    @JsonProperty("html_url")
    val htmlUrl: String,
    @JsonProperty("followers_url")
    val followersUrl: String,
    @JsonProperty("following_url")
    val followingUrl: String,
    @JsonProperty("gists_url")
    val gistsUrl: String,
    @JsonProperty("starred_url")
    val starredUrl: String,
    @JsonProperty("subscriptions_url")
    val subscriptionsUrl: String,
    @JsonProperty("organizations_url")
    val organizationsUrl: String,
    @JsonProperty("repos_url")
    val reposUrl: String,
    @JsonProperty("events_url")
    val eventsUrl: String,
    @JsonProperty("received_events_url")
    val receivedEventsUrl: String,
    val type: String,
    @JsonProperty("user_view_type")
    val userViewType: String?,
    @JsonProperty("site_admin")
    val siteAdmin: Boolean
)
