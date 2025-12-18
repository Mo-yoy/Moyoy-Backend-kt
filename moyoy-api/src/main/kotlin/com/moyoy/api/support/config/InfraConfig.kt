package com.moyoy.api.support.config

import com.moyoy.infra.EnableMoyoyConfig
import com.moyoy.infra.MoyoyConfigGroup
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableMoyoyConfig([MoyoyConfigGroup.JPA])
class InfraConfig
