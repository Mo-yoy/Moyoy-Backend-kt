package com.moyoy.api.user.application

import com.moyoy.common.const.GithubAttributes
import com.moyoy.domain.user.Role
import com.moyoy.domain.user.SocialSize
import com.moyoy.domain.user.User
import com.moyoy.domain.user.UserRepository
import com.moyoy.domain.user.dto.UserCreateDto
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// / TODO : 로깅
@Service
class RegisterUserOrSyncUsecase(
    private val userRepository: UserRepository
) {
    data class Input(
        val githubUserId: Int,
        val username: String,
        val profileImgUrl: String,
        val type: String,
        val followers: Int,
        val following: Int
    ) {
        companion object {
            fun from(oAuth2User: OAuth2User): Input {
                val attrs = oAuth2User.attributes

                return Input(
                    githubUserId = attrs[GithubAttributes.ID] as Int,
                    username = attrs[GithubAttributes.LOGIN] as String,
                    profileImgUrl = attrs[GithubAttributes.AVATAR_URL] as String,
                    type = attrs[GithubAttributes.TYPE] as String,
                    followers = attrs[GithubAttributes.FOLLOWERS] as Int,
                    following = attrs[GithubAttributes.FOLLOWING] as Int
                )
            }
        }
    }

    data class Output(
        val id: Long,
        val githubUserId: Int,
        val username: String,
        val profileImgUrl: String,
        val socialSize: SocialSize,
        val role: Role
    ) {
        companion object {
            fun from(user: User): Output {
                return Output(
                    id = requireNotNull(user.id) { "유저 데이터 동기화후 id는 null 일 수 없습니다." },
                    githubUserId = user.githubUserId,
                    username = user.username,
                    profileImgUrl = user.profileImgUrl,
                    socialSize = user.socialSize,
                    role = user.role
                )
            }
        }
    }

    @Transactional
    fun execute(input: Input): Output {
        val user = userRepository.findByGithubUserId(input.githubUserId)

        val resultUser =
            user ?.let {
                sync(it, input)
            } ?: register(input)

        return resultUser
    }

    private fun sync(
        user: User,
        input: Input
    ): Output {
        val socialSize = SocialSize.of(input.followers, input.following)
        user.changeSocialSize(socialSize)
        user.changeProfile(input.username, input.profileImgUrl)

        return Output.from(user)
    }

    private fun register(input: Input): Output {
        val socialSize = SocialSize.of(input.followers, input.following)
        val userCreateDto =
            UserCreateDto.of(
                input.githubUserId,
                input.username,
                input.profileImgUrl,
                socialSize
            )

        val newUser = User.from(userCreateDto)
        val savedUser = userRepository.save(newUser)
        return Output.from(savedUser)
    }
}
