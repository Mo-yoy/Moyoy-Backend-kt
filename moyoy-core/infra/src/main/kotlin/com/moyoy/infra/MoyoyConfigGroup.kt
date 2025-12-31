package com.moyoy.infra

import com.moyoy.infra.feign.FeignConfig
import com.moyoy.infra.jasypt.JasyptConfig
import com.moyoy.infra.jpa.JpaConfig
import kotlin.reflect.KClass

enum class MoyoyConfigGroup(
    val configClass: KClass<out MoyoyConfig>
) {
    JPA(JpaConfig::class),
    JASYPT(JasyptConfig::class),
    FEIGN(FeignConfig::class)
}
