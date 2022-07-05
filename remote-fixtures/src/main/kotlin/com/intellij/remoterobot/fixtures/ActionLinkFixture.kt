package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import com.intellij.ui.components.labels.ActionLink

@DefaultXpath(by = "ActionLink type", xpath = "//div[@class='ActionLink']")
@FixtureName("Action link")
class ActionLinkFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byText(text: String) =
            Locators.byTypeAndProperties(ActionLink::class.java, Locators.XpathProperty.TEXT to text)

        @JvmStatic
        fun byTextContains(text: String) =
            Locators.byTypeAndPropertiesContains(ActionLink::class.java, Locators.XpathProperty.TEXT to text)
    }
}