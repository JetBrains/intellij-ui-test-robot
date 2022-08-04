// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.js

import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext


interface JavaScriptExecutor {
    fun execute(script: String, componentContext: ComponentContext): Any?
    fun execute(script: String, robotContext: RobotContext): Any?
}