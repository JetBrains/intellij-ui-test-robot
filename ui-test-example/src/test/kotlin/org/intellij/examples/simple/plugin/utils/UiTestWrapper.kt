// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin.utils

import com.intellij.remoterobot.RemoteRobot
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


fun uiTest(url: String = "http://127.0.0.1:8082", test: RemoteRobot.() -> Unit) {
    // See retrofit rest calls in the logs for debug
    // https://stackoverflow.com/questions/45646188/how-can-i-debug-my-retrofit-api-call
    val remoteRobot: RemoteRobot = if (System.getProperty("debug-retrofit")?.equals("enable") == true) {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()
        RemoteRobot(url, client)
    } else {
        RemoteRobot(url)
    }

    remoteRobot.apply(test)
}

