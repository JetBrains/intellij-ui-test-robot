package com.intellij.remoterobot.services.js

import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext


interface JavaScriptExecutor {
    fun execute(script: String, componentContext: ComponentContext): Any?
    fun execute(script: String, robotContext: RobotContext): Any?
}