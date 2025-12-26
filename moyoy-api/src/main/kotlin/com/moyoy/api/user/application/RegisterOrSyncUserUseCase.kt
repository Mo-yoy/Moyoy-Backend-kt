package com.moyoy.api.user.application

import com.moyoy.common.const.GithubAttributes
import com.moyoy.domain.user.GithubProfile
import com.moyoy.domain.user.Role
import com.moyoy.domain.user.SocialSize
import com.moyoy.domain.user.User
import com.moyoy.domain.user.UserCreate
import com.moyoy.domain.user.UserRepository
import com.moyoy.domain.user.UserSync
import com.moyoy.domain.user.error.UserGithubAccountTypeNotAllowException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterOrSyncUserUseCase(
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
                    id = user.id!!,
                    githubUserId = user.githubUserId,
                    username = user.githubProfile.username,
                    profileImgUrl = user.githubProfile.profileImgUrl,
                    socialSize = user.socialSize,
                    role = user.role
                )
            }
        }
    }

    @Transactional
    fun execute(input: Input): Output {
        isGithubUserAccountType(input.type)

        val socialSize =
            classifyUserSocialSize(
                followers = input.followers,
                following = input.following
            )

        val user =
            userRepository
                .findByGithubUserId(input.githubUserId)
                ?.apply { syncUser(this, input, socialSize) }
                ?: registerNewUser(input, socialSize)

        return Output.from(user)
    }

    private fun isGithubUserAccountType(type: String) {
        if (type != GithubAttributes.USER_TYPE) {
            throw UserGithubAccountTypeNotAllowException()
        }
    }

    private fun classifyUserSocialSize(
        followers: Int,
        following: Int
    ): SocialSize {
        return SocialSize.of(
            followerCount = followers,
            followingCount = following
        )
    }

    private fun syncUser(
        user: User,
        input: Input,
        socialSize: SocialSize
    ) {
        val userSync =
            UserSync(
                githubProfile = GithubProfile(input.username, input.profileImgUrl),
                socialSize = socialSize
            )
        user.syncAccountWithGithub(userSync)
    }

    private fun registerNewUser(
        input: Input,
        socialSize: SocialSize
    ): User {
        val userCreate =
            UserCreate(
                githubUserId = input.githubUserId,
                githubProfile = GithubProfile(input.username, input.profileImgUrl),
                socialSize = socialSize
            )
        return userRepository.save(User.create(userCreate))
    }
}
