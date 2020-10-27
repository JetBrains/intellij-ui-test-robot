// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.encryption

interface Encryptor {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}