// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.ide.AppLifecycleListener
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCache
import com.intellij.remoterobot.services.IdeRobot
import com.intellij.remoterobot.services.LambdaLoader
import com.intellij.remoterobot.services.js.RhinoJavaScriptExecutor

class RobotServerStarter : AppLifecycleListener {

    override fun appFrameCreated(commandLineArgs: List<String>) {
        val serverHost = if (System.getProperty("robot-server.host.public")?.toBoolean() == true) {
                "0.0.0.0"
            } else {
                "127.0.0.1"
            }
        val serverPort = System.getProperty("robot-server.port")?.toIntOrNull() ?: 8580
        TextToKeyCache.init(javaClass.classLoader)
        RobotServerImpl(serverHost, serverPort) { IdeRobot(TextToKeyCache, RhinoJavaScriptExecutor(), LambdaLoader()) }.startServer()
    }
}