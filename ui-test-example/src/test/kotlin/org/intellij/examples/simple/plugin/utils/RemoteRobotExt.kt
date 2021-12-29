package org.intellij.examples.simple.plugin.utils

import com.intellij.remoterobot.RemoteRobot

fun RemoteRobot.isAvailable(): Boolean = runCatching {
    callJs<Boolean>("true")
}.getOrDefault(false)
