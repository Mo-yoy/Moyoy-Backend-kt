package com.moyoy.domain

import com.moyoy.domain.user.Role
import com.moyoy.domain.user.SocialSize
import com.moyoy.domain.user.User
import com.moyoy.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class BaseEntityTest
    @Autowired
    constructor(
        val entityManager: TestEntityManager,
        val userRepository: UserRepository
    ) {
        @Test
        @DisplayName("엔티티 저장 시 createdAt에 현재 시간이 기록된다")
        fun checkCreationTimestamp() {
            // given
            val user =
                User(
                    githubUserId = 1,
                    username = "testUser",
                    profileImgUrl = "url",
                    socialSize = SocialSize.SMALL,
                    role = Role.USER
                )

            // when
            // Generated Type Identity라 바로 반영
            val savedUser = userRepository.save(user)

            // then
            assertThat(savedUser.createdAt).isAfter(LocalDateTime.MIN)
            assertThat(savedUser.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        }

        @Test
        @DisplayName("엔티티 수정 시 updatedAt이 갱신된다")
        fun checkUpdateTimestamp() {
            // given
            val user =
                userRepository.save(
                    User(
                        githubUserId = 2,
                        username = "oldName",
                        profileImgUrl = "url",
                        socialSize = SocialSize.SMALL,
                        role = Role.USER
                    )
                )

            val initialUpdateTime = user.updatedAt

            // when
            Thread.sleep(100)
            user.changeProfile("newName", "newUrl")
            userRepository.saveAndFlush(user)

            // then
            assertThat(user.updatedAt).isAfter(initialUpdateTime)
        }
    }
