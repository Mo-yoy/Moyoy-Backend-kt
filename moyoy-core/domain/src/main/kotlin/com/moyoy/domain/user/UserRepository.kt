package com.moyoy.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByGithubUserId(githubUserId: Int): User?
}
