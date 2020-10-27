// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

@file:Suppress("NOTHING_TO_INLINE")

package com.intellij.remoterobot.data

import com.intellij.remoterobot.utils.Color
import com.intellij.remoterobot.utils.color
import org.assertj.swing.core.Robot
import java.awt.Component
import java.lang.ref.WeakReference
import java.util.*

interface RemoteLoggableContext {
    val robot: Robot
    val log: RemoteLogger
}
class RobotContext(override val robot: Robot): RemoteLoggableContext {
    override val log = RemoteLogger()
}

class ComponentContext(override val robot: Robot, component: Component): RemoteLoggableContext {
    override val log = RemoteLogger()
    val objects = mutableMapOf<String, Any>()
    val component: Component get() = componentRef.get() ?: throw IllegalStateException("the component is not available")
    private val componentRef = WeakReference(component)
}

class RemoteLogger {
    private val logMessages = StringBuilder()
    fun info(message: String): StringBuilder = logMessages.append("${Date()} INFO :  $message\n".color(
        Color.YELLOW))
    fun warn(message: String): StringBuilder = logMessages.append("${Date()} WARN :  $message\n".color(
        Color.RED))
    fun error(message: String): StringBuilder = logMessages.append("${Date()} ERROR :  $message\n".color(
        Color.RED))
    fun getLog(): String {
        val text = logMessages.toString()
        logMessages.clear()
        return text
    }
}

inline fun <reified T : Any> ComponentContext.componentAs(): T {
    return component as T
}

inline fun <reified T : Any> ComponentContext.save(name: String, obj: T) {
    this.objects[name] = obj
}

inline fun <reified T : Any> ComponentContext.load(name: String): T {
    return this.objects[name] as T
}