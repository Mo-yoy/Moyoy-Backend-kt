package com.moyoy.api.support.config

import com.moyoy.infra.EnableMoyoyConfig
import com.moyoy.infra.MoyoyConfigGroup.FEIGN
import com.moyoy.infra.MoyoyConfigGroup.JASYPT
import com.moyoy.infra.MoyoyConfigGroup.JPA
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableMoyoyConfig(
    [
        JPA,
        JASYPT,
        FEIGN
    ]
)
class InfraConfig
