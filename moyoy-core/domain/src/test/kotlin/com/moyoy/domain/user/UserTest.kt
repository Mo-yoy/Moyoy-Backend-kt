package com.moyoy.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {
    private fun createTestUser(): User {
        return User(
            id = 1L,
            githubUserId = 12345,
            username = "oldName",
            profileImgUrl = "https://old.img",
            socialSize = SocialSize.SMALL,
            role = Role.USER
        )
    }

    @Test
    @DisplayName("changeProfile 호출 시, 프로필(이름, 이미지) 정보가 정상 변경된다.")
    fun changeProfile() {
        // given
        val user = createTestUser()
        val newUsername = "newName"
        val newImageUrl = "https://new.img"

        // when
        user.changeProfile(newUsername, newImageUrl)

        // then
        assertThat(user.username).isEqualTo(newUsername)
        assertThat(user.profileImgUrl).isEqualTo(newImageUrl)
    }

    @Test
    @DisplayName("chageSocialSize 호출 시, 소셜 사이즈가 정상 변경되어야 한다")
    fun changeSocialSize() {
        // given
        val user = createTestUser()
        val newSize = SocialSize.LARGE

        // when
        user.changeSocialSize(newSize)

        // then
        assertThat(user.socialSize).isEqualTo(newSize)
    }
}
