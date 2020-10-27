// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName

class RetrieveResponse(
    @SerializedName("bytes") val data: ByteArray?,
    @SerializedName("exception") val exception: Throwable?,
    @SerializedName("message") val message: String?,
    @SerializedName("log") val log: String
)