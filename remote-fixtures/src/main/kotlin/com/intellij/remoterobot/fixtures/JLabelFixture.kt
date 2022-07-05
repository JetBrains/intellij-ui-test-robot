package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.ui.components.labels.LinkLabel
import javax.swing.JLabel

@DefaultXpath(by = "JLabel type", xpath = "//div[@class='JLabel' or @class='JBLabel']")
@FixtureName("JLabel")
class JLabelFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun byText(text: String) =
            Locators.byTypeAndProperties(JLabel::class.java, Locators.XpathProperty.TEXT to text)

        @JvmStatic
        fun byContainsText(text: String) =
            Locators.byTypeAndPropertiesContains(JLabel::class.java, Locators.XpathProperty.TEXT to text)

        @JvmStatic
        fun byLinkLabelText(linkLabelText: String) =
            Locators.byTypeAndPropertiesContains(LinkLabel::class.java, Locators.XpathProperty.TEXT to linkLabelText)
    }

    val value: String
        get() = step("..get value") { callJs("""component.getText() || """"", true) }

    fun isVisible(): Boolean = step("..is 'JBLabel' visible") {
        callJs("component.isVisible();", true)
    }
}