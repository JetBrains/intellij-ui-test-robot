package com.intellij.remoterobot.launcher

import com.intellij.remoterobot.RemoteRobot

fun RemoteRobot.isAvailable(): Boolean = runCatching {
    callJs<Boolean>("true")
}.getOrDefault(false)