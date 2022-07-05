package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.RelativeLocators
import javax.swing.JTextField

class JTextFieldFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JTextField::class.java)

        @JvmStatic
        fun byLabel(fixture: JLabelFixture): Locator {
            return RelativeLocators.byLabel<JTextField>(fixture)
        }
    }

    var text: String
        set(value) = step("Set text '$value'") {
            runJs("JTextComponentFixture(robot, component).setText('${value.replace("\\", "\\\\")}')")
        }
        get() = step("Get text") {
            callJs("component.getText() || ''", true)
        }

    val isEnabled: Boolean
        get() = step("..is enabled?") {
            callJs("component.isEnabled()", true)
        }
}