package com.intellij.remoterobot.fixtures


import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.RelativeLocators
import javax.swing.JTextArea

@FixtureName("JTextArea")
class JTextAreaFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {
        fun byType() = Locators.byType(JTextArea::class.java)

        fun byLabel(fixture: JLabelFixture): Locator {
            return RelativeLocators.byLabel<JTextArea>(fixture)
        }
    }

    val text: String
        get() = step("Get text") {
            callJs("""component.getText() || """"", true)
        }
}