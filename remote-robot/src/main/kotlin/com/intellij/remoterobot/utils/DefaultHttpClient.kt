// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.utils

import okhttp3.OkHttpClient
import java.time.Duration

object DefaultHttpClient {
    val client = OkHttpClient.Builder().readTimeout(Duration.ofSeconds(30)).build()
}