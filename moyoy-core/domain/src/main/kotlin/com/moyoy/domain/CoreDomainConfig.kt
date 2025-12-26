package com.moyoy.domain

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@EntityScan("com.moyoy")
@EnableJpaRepositories("com.moyoy")
class CoreDomainConfig
