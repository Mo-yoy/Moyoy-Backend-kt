package com.moyoy.infra

import com.moyoy.infra.jpa.JpaConfig
import kotlin.reflect.KClass

enum class MoyoyConfigGroup(
    val configClass: KClass<out MoyoyConfig>
) {
    JPA(JpaConfig::class)
}
