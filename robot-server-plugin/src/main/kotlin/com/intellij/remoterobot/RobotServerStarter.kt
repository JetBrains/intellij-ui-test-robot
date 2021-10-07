// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.ide.ApplicationInitializedListener
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCacheGlobal
import com.intellij.remoterobot.services.IdeRobot
import com.intellij.remoterobot.services.LambdaLoader
import com.intellij.remoterobot.services.js.RhinoJavaScriptExecutor

class RobotServerStarter : ApplicationInitializedListener {

    override fun componentsInitialized() {
        val serverHost = if (System.getProperty("robot-server.host.public")?.toBoolean() == true) {
                "0.0.0.0"
            } else {
                "127.0.0.1"
            }
        val serverPort = System.getProperty("robot-server.port")?.toIntOrNull() ?: 8580
        val textToKeyCache = TextToKeyCacheGlobal.cache
        RobotServerImpl(serverHost, serverPort) { IdeRobot(textToKeyCache, RhinoJavaScriptExecutor(), LambdaLoader()) }.startServer()
    }
}