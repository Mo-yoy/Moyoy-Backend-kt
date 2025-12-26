package com.moyoy.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

// 현재는 Github 프로필과 우리 서비스의 프로필이 동일하고, 우리 서비스만의 별도 프로필은 존재하지 않음
@Embeddable
data class GithubProfile(
    @Column(nullable = false)
    val username: String,
    @Column(nullable = false)
    val profileImgUrl: String
)
