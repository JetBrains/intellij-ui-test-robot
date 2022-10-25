// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
import java.awt.Color
import java.awt.Point

open class ComponentFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : Fixture(remoteRobot, remoteComponent) {

    open fun click(): Unit = step("..click") {
        runJs("robot.click(component);")
    }

    open fun doubleClick(): Unit = step("double click") {
        runJs("robot.doubleClick(component);")
    }

    open fun rightClick(): Unit = step("right click") {
        runJs("robot.rightClick(component);")
    }

    // click(c: Component, where: Point, button: MouseButton, times: Int)
    open fun rightDoubleClick(): Unit = step("right double click") {
        runJs("robot.click(component, null, MouseButton.RIGHT_BUTTON, 2)")
    }


    open fun click(where: Point): Unit = step("..click at ${where.x}:${where.y}") {
        runJs(
            """
            const point = new java.awt.Point(${where.x}, ${where.y});
            robot.click(component, point);
        """
        )
    }

    open fun doubleClick(where: Point): Unit = step("double click at ${where.x}:${where.y}") {
        runJs(
            """
           const point = new java.awt.Point(${where.x}, ${where.y}); 
           robot.click(component, point, MouseButton.LEFT_BUTTON, 2)
        """
        )
    }

    open fun rightClick(where: Point): Unit = step("right click at ${where.x}:${where.y}") {
        runJs(
            """
           const point = new java.awt.Point(${where.x}, ${where.y}); 
           robot.click(component, point, MouseButton.RIGHT_BUTTON, 1)
        """
        )
    }

    open fun rightDoubleClick(where: Point): Unit = step("right double click at ${where.x}:${where.y}") {
        runJs(
            """
           const point = new java.awt.Point(${where.x}, ${where.y}); 
           robot.click(component, point, MouseButton.RIGHT_BUTTON, 2)
        """
        )
    }

    open fun getBackgroundColor(): Color {
        return Color(this.callJs("component.background.getRGB()"))
    }

    open fun getForegroundColor(): Color {
        return Color(this.callJs("component.foreground.getRGB()"))
    }
}