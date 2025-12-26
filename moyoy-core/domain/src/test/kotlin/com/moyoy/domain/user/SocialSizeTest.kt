package com.moyoy.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SocialSizeTest {
    @DisplayName("팔로워와 팔로잉 수의 합에 따라 알맞은 SocialSize가 반환된다")
    @ParameterizedTest(name = "팔로워 {0} + 팔로잉 {1} -> {2}")
    @CsvSource(
        // 경계값: SMALL (0 ~ 99)
        "0, 0, SMALL",
        "50, 49, SMALL",
        // 경계값: MEDIUM (100 ~ 499)
        "50, 50, MEDIUM",
        "200, 299, MEDIUM",
        // 경계값: LARGE (500 ~ 999)
        "250, 250, LARGE",
        "500, 499, LARGE",
        // 경계값: HUGE (1000 이상)
        "500, 500, HUGE",
        "10000, 5000, HUGE"
    )
    fun classifyTest(
        followerCount: Int,
        followingCount: Int,
        expectedSize: SocialSize
    ) {
        // when
        val result =
            SocialSize.of(
                followerCount = followerCount,
                followingCount = followingCount
            )

        // then
        assertThat(result).isEqualTo(expectedSize)
    }
}
