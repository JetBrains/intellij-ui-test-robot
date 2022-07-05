package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.RelativeLocators
import javax.swing.JComboBox

@DefaultXpath(by = "JComboBox type", xpath = "//div[@class='JComboBox']")
@FixtureName("Combobox")
open class ComboBoxFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JComboBox::class.java)

        @JvmStatic
        fun byLabel(fixture: JLabelFixture): Locator {
            return RelativeLocators.byLabel<JComboBox<*>>(fixture)
        }
    }

    init {
        runJs(
            """
            const fixture = new JComboBoxFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JComboBoxTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("fixture", fixture) 
        """
        )
    }

    fun selectItem(text: String) = step("Select '$text'") {
        if (listValues().contains(text).not()) {
            click()
        }
        runJs("""ctx.get('fixture').selectItem("$text")""")
    }

    open fun selectItemContains(text: String) = step("Select '$text'") {
        if (listValues().singleOrNull { it.contains(text) } == null) {
            click()
        }
        selectItem(listValues().single { it.contains(text) })
    }

    /*
    Returns selected text or empty String if selectedText is null
     */
    fun selectedText(): String = step("Get Selected text") {
        return@step callJs("""
            const text = ctx.get('fixture').selectedItem();
            if (text) {
                text;
            } else {
                "";
            }
        """)
    }

    fun listValues(): List<String> {
        return callJs<Array<String>>("ctx.get('fixture').contents()").toList()
    }
}

