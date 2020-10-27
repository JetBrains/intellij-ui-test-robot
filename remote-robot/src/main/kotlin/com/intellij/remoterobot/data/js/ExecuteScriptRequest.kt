// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.data.js

import com.intellij.remoterobot.encryption.Encryptor

data class ExecuteScriptRequest(val script: String, val runInEdt: Boolean) {
    fun encrypt(encryptor: Encryptor): ExecuteScriptRequest {
        return ExecuteScriptRequest(encryptor.encrypt(script), runInEdt)
    }

    fun decrypt(encryptor: Encryptor): ExecuteScriptRequest {
        return ExecuteScriptRequest(encryptor.decrypt(script), runInEdt)
    }
}