package com.moyoy.domain.user

enum class Role(
    val value: String
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ANONYMOUS("ROLE_ANONYMOUS")
}
