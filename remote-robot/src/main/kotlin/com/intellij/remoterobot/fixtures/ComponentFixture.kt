package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
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

    open fun click(where: Point): Unit = step("..click at ${where.x}:${where.y}") {
        runJs(
            """
            const point = new java.awt.Point(${where.x}, ${where.y});
            robot.click(component, point);
        """
        )
    }
}