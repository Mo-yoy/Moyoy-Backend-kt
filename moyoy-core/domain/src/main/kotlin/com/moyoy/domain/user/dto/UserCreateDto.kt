package com.moyoy.domain.user.dto

import com.moyoy.domain.user.SocialSize

data class UserCreateDto(
    val githubUserId: Int,
    val username: String,
    val profileImgUrl: String,
    val socialSize: SocialSize
) {
    companion object {
        fun of(
            githubUserId: Int,
            username: String,
            profileImgUrl: String,
            socialSize: SocialSize
        ): UserCreateDto = UserCreateDto(githubUserId, username, profileImgUrl, socialSize)
    }
}
