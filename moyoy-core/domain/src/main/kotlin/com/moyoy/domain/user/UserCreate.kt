package com.moyoy.domain.user

data class UserCreate(
    val githubUserId: Int,
    val githubProfile: GithubProfile,
    val socialSize: SocialSize
)
