package com.moyoy.infra.github.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubUserInfoResponse(
    val login: String,
    val id: Int,
    @get:JsonProperty("node_id") val nodeId: String? = null,
    @get:JsonProperty("avatar_url") val avatarUrl: String? = null,
    @get:JsonProperty("gravatar_id") val gravatarId: String? = null,
    val url: String? = null,
    @get:JsonProperty("html_url") val htmlUrl: String? = null,
    @get:JsonProperty("followers_url") val followersUrl: String? = null,
    @get:JsonProperty("following_url") val followingUrl: String? = null,
    @get:JsonProperty("gists_url") val gistsUrl: String? = null,
    @get:JsonProperty("starred_url") val starredUrl: String? = null,
    @get:JsonProperty("subscriptions_url") val subscriptionsUrl: String? = null,
    @get:JsonProperty("organizations_url") val organizationsUrl: String? = null,
    @get:JsonProperty("repos_url") val reposUrl: String? = null,
    @get:JsonProperty("events_url") val eventsUrl: String? = null,
    @get:JsonProperty("received_events_url") val receivedEventsUrl: String? = null,
    val type: String? = "User",
    @get:JsonProperty("user_view_type") val userViewType: String? = null,
    @get:JsonProperty("site_admin") val siteAdmin: Boolean = false,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val hireable: Boolean? = null,
    val bio: String? = null,
    @get:JsonProperty("twitter_username") val twitterUsername: String? = null,
    @get:JsonProperty("public_repos") val publicRepos: Int = 0,
    @get:JsonProperty("public_gists") val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    @get:JsonProperty("created_at") val createdAt: String? = null,
    @get:JsonProperty("updated_at") val updatedAt: String? = null
)
