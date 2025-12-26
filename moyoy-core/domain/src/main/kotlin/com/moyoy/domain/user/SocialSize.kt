package com.moyoy.domain.user

enum class SocialSize(
    val threshold: Int
) {
    SMALL(0),
    MEDIUM(100),
    LARGE(500),
    HUGE(1000);

    companion object {
        fun of(
            followerCount: Int,
            followingCount: Int
        ): SocialSize {
            val totalCount = followerCount + followingCount

            return when {
                totalCount >= HUGE.threshold -> HUGE
                totalCount >= LARGE.threshold -> LARGE
                totalCount >= MEDIUM.threshold -> MEDIUM
                else -> SMALL
            }
        }
    }
}
