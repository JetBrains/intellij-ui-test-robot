// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.encryption

const val ENCRYPTION_ENABLED_KEY = "robot.encryption.enabled"
const val ENCRYPTION_PASSWORD_KEY = "robot.encryption.password"

class EncryptorFactory {
    fun getInstance(): Encryptor {
        val isTurnedOn = System.getProperty(ENCRYPTION_ENABLED_KEY)?.equals("true") ?: false
        if (isTurnedOn.not()) {
            return TurnedOffEncryptor()
        }
        val password =
            System.getProperty(ENCRYPTION_PASSWORD_KEY)
                ?: throw IllegalStateException("Specify '$ENCRYPTION_PASSWORD_KEY' property")
        return AesEncryptor(password)
    }
}