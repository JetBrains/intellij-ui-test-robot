package com.intellij.remoterobot.fixtures

import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.waitFor
import com.intellij.ui.components.BasicOptionButtonUI
import java.time.Duration
import javax.swing.JButton

@DefaultXpath(by = "JButton type", xpath = "//div[@class='JButton']")
@FixtureName("Button")
open class JButtonFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byText(text: String) = Locators.byTypeAndProperties(JButton::class.java, Locators.XpathProperty.TEXT to text)

        @JvmStatic
        fun byFixedSizeButtonType() = Locators.byType(FixedSizeButton::class.java)

        @JvmStatic
        fun byMainButtonType(text: String) =
            Locators.byTypeAndProperties(BasicOptionButtonUI.MainButton::class.java, Locators.XpathProperty.TEXT to text)
    }

    val text: String
        get() = callJs("""component.getText() || """"", true)

    val icon: String
        get() = callJs("component.getIcon().toString()", true)

    fun clickWhenEnabled() {
        waitFor(Duration.ofSeconds(5)) {
            isEnabled()
        }
        click()
    }

    open fun isEnabled(): Boolean {
        return callJs("component.isEnabled();", true)
    }
}