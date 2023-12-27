package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import javax.swing.JList

class JListFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ContainerFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JList::class.java)

        @JvmStatic
        fun byItem(itemText: String) =
            Locators.byTypeAndPropertiesContains(JList::class.java, Locators.XpathProperty.TEXT to itemText)
    }

    init {
        runJs(
            """
            const fixture = new JListFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JListTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("cellReader", cellReader)
            ctx.put("fixture", fixture) 
        """
        )
    }

    /*
    Overrides cell width which will be used to parse text in it. JList::width is used as default, but sometimes you might need to make it bigger
    https://github.com/JetBrains/intellij-ui-test-robot/issues/383
     */
    fun setCellReaderWidth(width: Int) = runJs("ctx.get('cellReader').setCellWidth($width)")

    fun collectItems() = callJs<Array<String>>("ctx.get('fixture').contents()").toList()

    fun collectSelectedItems() = callJs<Array<String>>("ctx.get('fixture').selection()").toList()

    fun clickItem(itemText: String, fullMatch: Boolean = true) {
        findItemIndex(itemText, fullMatch)?.let {
            clickItemAtIndex(it)
        } ?: throw IllegalArgumentException("item with text $itemText not found")
    }

    fun clickItemAtIndex(index: Int) {
        runJs("ctx.get('fixture').clickItem($index)")
    }

    private fun findItemIndex(itemText: String, fullMatch: Boolean): Int? =
        collectItems().indexOfFirst {
            if (fullMatch) it == itemText
            else it.contains(itemText, true)
        }.let {
            if (it == -1) null
            else it
        }
}