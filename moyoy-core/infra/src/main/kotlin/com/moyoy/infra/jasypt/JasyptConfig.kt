package com.moyoy.infra.jasypt

import com.moyoy.infra.MoyoyConfig
import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.context.annotation.Bean

class JasyptConfig(
    private val jasyptProperties: JasyptProperties
) : MoyoyConfig {
    @Bean
    fun internalEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()

        config.password = jasyptProperties.password
        config.algorithm = jasyptProperties.algorithm
        config.setPoolSize(jasyptProperties.poolSize)

        encryptor.setConfig(config)
        return encryptor
    }

    @Bean
    fun jasyptStringEncryptor(internalEncryptor: StringEncryptor): JasyptStringEncryptor {
        return JasyptStringEncryptor(internalEncryptor)
    }
}
