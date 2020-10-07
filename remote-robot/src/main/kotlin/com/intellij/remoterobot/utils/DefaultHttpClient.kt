package com.intellij.remoterobot.utils

import okhttp3.OkHttpClient
import java.time.Duration

object DefaultHttpClient {
    val client = OkHttpClient.Builder().readTimeout(Duration.ofSeconds(30)).build()
}