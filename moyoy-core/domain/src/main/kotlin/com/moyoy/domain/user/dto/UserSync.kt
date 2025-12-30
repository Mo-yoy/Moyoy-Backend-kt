package com.moyoy.domain.user.dto

import com.moyoy.domain.user.GithubProfile
import com.moyoy.domain.user.SocialSize

data class UserSync(
    val githubProfile: GithubProfile,
    val socialSize: SocialSize
)
