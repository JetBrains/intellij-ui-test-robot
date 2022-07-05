package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.getSystemProperty
import javax.swing.JMenu
import javax.swing.JMenuBar

open class JMenuBarFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JMenuBar::class.java)
    }

    open fun select(vararg items: String) {
        if (remoteRobot.isMac() && isMacMenuEnabled()) {
            selectOnMac(*items)
        } else {
            openMenu(items[0]).select(*items.sliceArray(1..items.lastIndex))
        }
    }

    private fun openMenu(menuItem: String): JPopupMenuFixture = step("Open menu '$menuItem'") {
        find<ComponentFixture>(
            Locators.byTypeAndProperties(JMenu::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to menuItem)
        ).click()
        jPopupMenu().also {
            it.runJs(
                """
                robot.moveMouse(component, new Point(0, 0))
                const point = new Point(5, component.getHeight()/2)
                robot.moveMouse(component, point)
            """
            )
        }
    }

    private fun selectOnMac(vararg items: String) {
        val appName =
            remoteRobot.callJs<String>("com.intellij.openapi.application.ApplicationNamesInfo.getInstance().productName")
        val processName = when {
            appName.contains("IDEA", true) -> "idea"
            appName.contains("CLion", true) -> "clion"
            appName.contains("WebStorm", true) -> "webstorm"
            appName.contains("RubyMine", true) -> "rubymine"
            appName.contains("AppCode", true) -> "appcode"
            appName.contains("Studio", true) -> "studio"
            appName.contains("GoLand", true) -> "goland"
            appName.contains("PyCharm", true) -> "pycharm"
            appName.contains("Rider", true) -> "rider"
            appName.contains("PhpStorm", true) -> "phpstorm"
            appName.contains("DataGrip", true) -> "datagrip"
            appName.contains("MPS", true) -> "mps"
            else -> throw IllegalStateException("unknown ide")
        }
        val command = buildString {
            append(
                """
                    tell application "System Events"
                    tell process "$processName"
                    tell menu bar 1
                    tell menu bar item "${items.first()}"
                    tell menu "${items.first()}"
                """.trimIndent()
            )
            append("\n")

            for (item in items.sliceArray(1 until items.lastIndex)) {
                append("tell menu item \"$item\"\n")
                append("tell menu \"$item\"\n")
            }
            append("click menu item \"${items.last()}\"\n")

            append("end tell\n".repeat(2 * (items.size - 2)))

            append("end tell\n".repeat(5))
        }.replace("\n", "\\n")
        remoteRobot.runJs("java.lang.Runtime.getRuntime().exec(['osascript', '-e', '$command'])")
    }

    private fun isMacMenuEnabled() = remoteRobot.getSystemProperty("apple.laf.useScreenMenuBar").toBoolean()
            || remoteRobot.getSystemProperty("jbScreenMenuBar.enabled").toBoolean()
}
