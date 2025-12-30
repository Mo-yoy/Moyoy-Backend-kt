package com.moyoy.domain.user

import com.moyoy.domain.BaseEntity
import com.moyoy.domain.user.dto.UserCreate
import com.moyoy.domain.user.dto.UserSync
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class User(
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val githubUserId: Int,
    @Embedded
    var githubProfile: GithubProfile,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var socialSize: SocialSize,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role
) : BaseEntity() {
    companion object {
        fun create(userCreate: UserCreate): User {
            return User(
                githubUserId = userCreate.githubUserId,
                githubProfile = userCreate.githubProfile,
                socialSize = userCreate.socialSize,
                role = Role.USER
            )
        }
    }

    fun syncAccountWithGithub(userSync: UserSync) {
        changeGithubProfile(userSync.githubProfile)
        changeSocialSize(userSync.socialSize)
    }

    private fun changeGithubProfile(githubProfile: GithubProfile) {
        this.githubProfile = githubProfile
    }

    private fun changeSocialSize(socialSize: SocialSize) {
        this.socialSize = socialSize
    }
}
