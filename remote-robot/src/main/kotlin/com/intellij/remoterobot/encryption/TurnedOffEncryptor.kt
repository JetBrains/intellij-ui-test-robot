// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.encryption

class TurnedOffEncryptor : Encryptor {
    override fun encrypt(text: String): String {
        return text
    }

    override fun decrypt(text: String): String {
        return text
    }

}