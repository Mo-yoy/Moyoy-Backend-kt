package com.moyoy.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {
    private fun createTestUser(): User {
        return User(
            id = 1L,
            githubUserId = 12345,
            githubProfile = GithubProfile(username = "oldName", profileImgUrl = "https://old.img"),
            socialSize = SocialSize.SMALL,
            role = Role.USER
        )
    }

    @Test
    @DisplayName("syncAccountWithGithub 호출 시, 프로필과 소셜사이즈가 정상적으로 동기화 된다.")
    fun syncAccountWithGithub() {
        // given
        val user = createTestUser()
        val newUsername = "newName"
        val newImageUrl = "https://new.img"
        val newSocialSize = SocialSize.LARGE

        val userSync =
            UserSync(
                githubProfile = GithubProfile(newUsername, newImageUrl),
                socialSize = newSocialSize
            )

        // when
        user.syncAccountWithGithub(userSync)

        // then
        assertThat(user.githubProfile).isEqualTo(GithubProfile(newUsername, newImageUrl))
        assertThat(user.socialSize).isEqualTo(newSocialSize)
    }
}
