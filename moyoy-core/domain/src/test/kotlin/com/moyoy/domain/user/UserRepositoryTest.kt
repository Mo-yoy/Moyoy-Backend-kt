package com.moyoy.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest(
    @Autowired
    val userRepository: UserRepository,
    @Autowired
    val entityManager: TestEntityManager
) {
    @DisplayName("GithubUserId를 통해 User를 조회할 수 있다.")
    @Test
    fun findByGithubUserId() {
        // given
        val githubUserId = 1
        val user =
            User(
                githubUserId = githubUserId,
                githubProfile = GithubProfile("testUser", "url"),
                socialSize = SocialSize.SMALL,
                role = Role.USER
            )

        userRepository.save(user)

        // save 시점 이후 id 할당
        val savedUserId = user.id
        entityManager.clear()

        // when
        val foundUser = userRepository.findByGithubUserId(githubUserId)

        // then
        assertThat(foundUser?.id).isEqualTo(savedUserId)
    }

    @DisplayName("존재하지 않는 GithubUserId로 조회하면 null을 반환한다.")
    @Test
    fun findByGithubUserIdReturnNull() {
        // given
        val nonExistentId = 1

        // when
        val foundUser = userRepository.findByGithubUserId(nonExistentId)

        // then
        assertThat(foundUser).isNull()
    }
}
