// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin.utils

import com.intellij.remoterobot.RemoteRobot

fun uiTest(url: String = "http://127.0.0.1:8082", test: RemoteRobot.() -> Unit) {
    RemoteRobot(url).apply(test)
}

