package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import javax.swing.JCheckBox

@DefaultXpath(by = "JCheckBox type", xpath = "//div[@class='JCheckBox' or @class='JBCheckBox']")
@FixtureName("Checkbox")
class JCheckboxFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun byText(text: String) = Locators.byTypeAndProperties(JCheckBox::class.java, Locators.XpathProperty.TEXT to text)

        @JvmStatic
        fun byTextContains(text: String) = Locators.byTypeAndPropertiesContains(JCheckBox::class.java, Locators.XpathProperty.TEXT to text)
    }

    val text: String
        get() = callJs("""component.getText() || """"", true)

    fun isSelected(): Boolean {
        return callJs("component.isSelected();", true)
    }

    fun select() {
        if (!isSelected()) this.click()
    }

    fun unselect() {
        if (isSelected()) this.click()
    }

    fun setValue(value: Boolean) =
        when (value) {
            true -> select()
            false -> unselect()
        }
}
