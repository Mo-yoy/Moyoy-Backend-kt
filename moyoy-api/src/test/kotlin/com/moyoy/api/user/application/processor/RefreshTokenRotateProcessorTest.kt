package com.moyoy.api.user.application.processor

import com.moyoy.api.auth.jwt.JwtRefreshWhiteList
import com.moyoy.api.auth.jwt.JwtRefreshWhiteListJDBCRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
class RefreshTokenRotateProcessorTest {
    @Autowired
    private lateinit var processor: RefreshTokenRotateProcessor

    @Autowired
    private lateinit var jwtRefreshWhiteListRepository: JwtRefreshWhiteListJDBCRepository

    @Test
    @DisplayName("rotate 수행 시, 기존 토큰은 삭제되고 새로운 토큰이 저장되어야 한다")
    fun rotateSuccessTest() {
        // given
        val oldTokenHash = "old-token-hash-test"
        val oldEntity =
            JwtRefreshWhiteList(
                userId = 100L,
                tokenHash = oldTokenHash,
                expiresAt = LocalDateTime.now().plusDays(1)
            )
        jwtRefreshWhiteListRepository.save(oldEntity)

        val newHash = "new-token-hash-test"
        val newTokenEntity =
            JwtRefreshWhiteList(
                userId = 100L,
                tokenHash = newHash,
                expiresAt = LocalDateTime.now().plusDays(1)
            )

        // when
        processor.rotate(oldTokenHash, newTokenEntity)

        // then
        val isOldTokenExists = jwtRefreshWhiteListRepository.existByTokenHash(oldTokenHash)
        assertThat(isOldTokenExists).isFalse()

        val isNewTokenExists = jwtRefreshWhiteListRepository.existByTokenHash(newHash)
        assertThat(isNewTokenExists).isTrue()
    }
}
