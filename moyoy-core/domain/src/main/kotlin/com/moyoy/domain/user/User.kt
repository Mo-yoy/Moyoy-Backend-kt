package com.moyoy.domain.user

import com.moyoy.domain.BaseEntity
import com.moyoy.domain.user.dto.UserCreateDto
import jakarta.persistence.Column
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
    @Column(nullable = false)
    var username: String,
    @Column(nullable = false)
    var profileImgUrl: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var socialSize: SocialSize,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role
) : BaseEntity() {
    companion object {
        fun from(userCreateDto: UserCreateDto): User {
            return User(
                githubUserId = userCreateDto.githubUserId,
                username = userCreateDto.username,
                profileImgUrl = userCreateDto.profileImgUrl,
                socialSize = userCreateDto.socialSize,
                role = Role.USER
            )
        }
    }

    fun changeProfile(
        username: String,
        profileImgUrl: String
    ) {
        this.username = username
        this.profileImgUrl = profileImgUrl
    }

    fun changeSocialSize(socialSize: SocialSize) {
        this.socialSize = socialSize
    }
}
