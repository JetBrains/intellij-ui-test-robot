// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures.dataExtractor

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.stepsProcessing.step
import org.assertj.swing.core.MouseButton

class RemoteText(private val fixture: Fixture, private val data: TextData) {
    val point
        get() = data.point
    val text
        get() = data.text

    @Suppress("LocalVariableName")
    private fun click(button: MouseButton, times: Int): Unit = step("..click at '$text'") {
        val _point = data.point
        val _button = button
        val _times = times
        fixture.remoteRobot.execute(fixture) {
            robot.click(component, _point, _button, _times)
        }
    }

    @JvmOverloads
    fun click(button: MouseButton = MouseButton.LEFT_BUTTON) {
        click(button, 1)
    }

    @JvmOverloads
    fun doubleClick(button: MouseButton = MouseButton.LEFT_BUTTON) {
        click(button, 2)
    }

    fun rightClick() {
        click(MouseButton.RIGHT_BUTTON, 1)
    }

    fun moveMouse() = step("move mouse to '$text'") {
        val _point = data.point
        fixture.execute { robot.moveMouse(component, _point) }
    }
}