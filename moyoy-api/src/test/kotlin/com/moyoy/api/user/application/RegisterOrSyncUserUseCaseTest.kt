package com.moyoy.api.user.application

import com.moyoy.common.const.GithubAttributes
import com.moyoy.domain.user.GithubProfile
import com.moyoy.domain.user.SocialSize
import com.moyoy.domain.user.User
import com.moyoy.domain.user.UserRepository
import com.moyoy.domain.user.dto.UserCreate
import com.moyoy.domain.user.error.UserGithubAccountTypeNotAllowException
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RegisterOrSyncUserUseCaseTest {
    @Autowired
    private lateinit var registerOrSyncUserUseCase: RegisterOrSyncUserUseCase

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    @DisplayName("Github 계정이 User 타입의 계정이 아닌 경우 UserGithubAccountTypeNotAllowException가 발생한다.")
    fun execute_fail_invalid_type() {
        // given
        val input =
            createInput(
                githubUserId = 1,
                type = "Organization"
            )

        // when & then
        assertThatThrownBy { registerOrSyncUserUseCase.execute(input) }
            .isInstanceOf(UserGithubAccountTypeNotAllowException::class.java)
    }

    @Test
    @DisplayName("우리 회원이 아니라면 정상적으로 회원가입 된다.")
    fun execute_success_new_user() {
        // given
        val newGithubId = 1234
        val input =
            createInput(
                githubUserId = newGithubId,
                username = "newbie",
                followers = 10,
                following = 10
            )

        // when
        val output = registerOrSyncUserUseCase.execute(input)
        entityManager.clear()

        // then
        assertThat(output.id).isNotNull()
        assertThat(output.githubUserId).isEqualTo(newGithubId)

        val savedUser = userRepository.findByGithubUserId(newGithubId)
        assertThat(savedUser).isNotNull
        assertThat(savedUser!!.id).isEqualTo(1L)
        assertThat(savedUser.githubUserId).isEqualTo(1234)
    }

    @Test
    @DisplayName("기존 회원이라면 프로필 업데이트를 진행한다.")
    fun execute_success_update_user() {
        // given
        val existingGithubId = 1234
        val alreadyExistedUser =
            User.create(
                UserCreate(
                    githubUserId = existingGithubId,
                    githubProfile = GithubProfile("old_name", "old_img"),
                    socialSize = SocialSize.SMALL
                )
            )
        userRepository.save(alreadyExistedUser)
        entityManager.clear()

        val input =
            createInput(
                githubUserId = existingGithubId,
                username = "updated_name",
                profileImgUrl = "new_img",
                followers = 1000,
                following = 0
            )

        // when
        val output = registerOrSyncUserUseCase.execute(input)
        entityManager.flush()
        entityManager.clear()

        // then
        val updatedUser = userRepository.findByGithubUserId(existingGithubId)!!
        assertThat(updatedUser.id).isEqualTo(alreadyExistedUser.id)
        assertThat(updatedUser.githubUserId).isEqualTo(existingGithubId)
        assertThat(updatedUser.githubProfile.username).isEqualTo("updated_name")
        assertThat(updatedUser.githubProfile.profileImgUrl).isEqualTo("new_img")
        assertThat(updatedUser.socialSize).isEqualTo(SocialSize.HUGE)
    }

    private fun createInput(
        githubUserId: Int,
        username: String = "test",
        profileImgUrl: String = "url",
        type: String = GithubAttributes.USER_TYPE,
        followers: Int = 0,
        following: Int = 0
    ): RegisterOrSyncUserUseCase.Input {
        return RegisterOrSyncUserUseCase.Input(
            githubUserId = githubUserId,
            username = username,
            profileImgUrl = profileImgUrl,
            type = type,
            followers = followers,
            following = following
        )
    }
}
