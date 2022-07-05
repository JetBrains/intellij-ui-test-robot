package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import javax.swing.JRadioButton

@DefaultXpath(by = "JRadioButton type", xpath = "//div[@class='JRadioButton' or @class='JBRadioButton']")
@FixtureName("RadioButton")
class JRadioButtonFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun byText(text: String) = Locators.byTypeAndProperties(JRadioButton::class.java, Locators.XpathProperty.TEXT to text)
    }

    val text: String
        get() = callJs("""component.getText() || """"", true)

    fun isSelected(): Boolean {
        return callJs("component.isSelected();", true)
    }

    fun select() {
        setValue(true)
    }

    fun unselect() {
        setValue(false)
    }

    fun setValue(value: Boolean) {
        if (isSelected() != value) click()
    }
}