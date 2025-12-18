package com.moyoy.infra.jasypt

import org.jasypt.encryption.StringEncryptor

class JasyptStringEncryptor(
    private val encryptor: StringEncryptor
) {
    fun encrypt(text: String): String {
        return encryptor.encrypt(text)
    }

    fun decrypt(encrypted: String): String {
        return encryptor.decrypt(encrypted)
    }
}
