package com.intellij.remoterobot.fixtures

import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators

@DefaultXpath(by = "ActionButton type", xpath = "//div[@class='ActionButton']")
@FixtureName("Action Button")
class ActionButtonFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {

        fun byType() = Locators.byType(ActionButton::class.java)

        fun byTooltipText(tooltipText: String) =
            Locators.byTypeAndProperties(ActionButton::class.java, Locators.XpathProperty.TOOLTIP to tooltipText)

        fun byTooltipTextContains(tooltipText: String) =
            Locators.byTypeAndPropertiesContains(ActionButton::class.java, Locators.XpathProperty.TOOLTIP to tooltipText)
    }

    val tooltipText: String
        get() = callJs("""component.getToolTipText() || """"", true)

    val templatePresentationText: String
        get() = callJs("""component.getAction().getTemplatePresentation().getText() || "";""", true)

    fun isEnabled(): Boolean = step("..is 'Action button' enabled") {
        callJs("component.isEnabled();", true)
    }

    fun popState(): PopState = step("..get pop state") {
        val popStateCode = callJs<Int>("component.getPopState()")
        return@step PopState.byInt(popStateCode)
    }

    enum class PopState(private val code: Int) {
        NORMAL(0),
        POPPED(1),
        PUSHED(-1),
        SELECTED(2);

        companion object {
            fun byInt(code: Int) = values().first { it.code == code }
        }
    }
}