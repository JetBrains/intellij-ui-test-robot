package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.utils.Locators
import javax.swing.JTable

open class JTableFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ContainerFixture(remoteRobot, remoteComponent) {

    companion object {
        fun byType() = Locators.byType(JTable::class.java)
    }

    init {
        runJs(
            """
            const fixture = new JTableFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JTableTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("fixture", fixture) 
        """
        )
    }

    val rowCount get() = callJs<Int>("""ctx.get("fixture").rowCount()""")

    val columnCount get() = callJs<Int>("""component.getColumnCount()""")

    fun collectItems(): List<List<String>> = callJs<Array<Array<String>>>("""ctx.get("fixture").contents()""")
        .map { it.toList() }

    fun selectedItem(): String = callJs("""ctx.get("fixture").selectionValue()""")

    fun clickCell(row: Int, column: Int) =
        runJs("""ctx.get("fixture").cell(org.assertj.swing.data.TableCell.row($row).column($column)).click()""")

    fun rightClickCell(row: Int, column: Int) =
        runJs("""ctx.get("fixture").cell(org.assertj.swing.data.TableCell.row($row).column($column)).rightClick()""")

    fun doubleClickCell(row: Int, column: Int) =
        runJs("""ctx.get("fixture").cell(org.assertj.swing.data.TableCell.row($row).column($column)).doubleClick()""")
}