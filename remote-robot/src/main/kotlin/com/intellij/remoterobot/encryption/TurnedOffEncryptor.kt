package com.intellij.remoterobot.encryption

class TurnedOffEncryptor : Encryptor {
    override fun encrypt(text: String): String {
        return text
    }

    override fun decrypt(text: String): String {
        return text
    }

}