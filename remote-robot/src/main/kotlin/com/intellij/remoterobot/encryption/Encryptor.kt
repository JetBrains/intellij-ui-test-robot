package com.intellij.remoterobot.encryption

interface Encryptor {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}