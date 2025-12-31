package com.moyoy.domain.follow

data class GithubFollowUserProfile(
    val githubId: Int,
    val username: String,
    val profileImgUrl: String
) : Comparable<GithubFollowUserProfile> {
    override fun compareTo(other: GithubFollowUserProfile): Int {
        return this.githubId.compareTo(other.githubId)
    }
}
