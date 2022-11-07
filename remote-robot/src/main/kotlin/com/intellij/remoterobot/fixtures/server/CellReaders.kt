// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures.server

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ui.MultilineTreeCellRenderer
import com.intellij.ui.SimpleColoredComponent
import org.assertj.swing.cell.JListCellReader
import org.assertj.swing.cell.JTableCellReader
import org.assertj.swing.cell.JTreeCellReader
import org.assertj.swing.driver.BasicJListCellReader
import org.assertj.swing.driver.BasicJTableCellReader
import org.assertj.swing.driver.BasicJTreeCellReader
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.edt.GuiQuery
import org.assertj.swing.exception.ComponentLookupException
import java.awt.Component
import java.awt.Container
import java.util.*
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JTable
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

@Deprecated("Will be replaced with external fixture lib")
class ExtendedJTreeCellReader : BasicJTreeCellReader(), JTreeCellReader {

    override fun valueAt(tree: JTree, modelValue: Any?): String? = valueAtExtended(tree, modelValue, false)

    fun valueAtExtended(tree: JTree, modelValue: Any?, isExtended: Boolean = true): String? {
        if (modelValue == null) return null
        val isLeaf: Boolean = try {
            modelValue is DefaultMutableTreeNode && modelValue.isLeaf
        }
        catch (e: Error) {
            false
        }
        return computeOnEdt {
            val cellRendererComponent = tree.cellRenderer.getTreeCellRendererComponent(tree, modelValue, false, false, isLeaf, 0, false)
            getValueWithCellRenderer(cellRendererComponent, isExtended)
        }
    }
}

@Deprecated("Will be replaced with external fixture lib")
class ExtendedJListCellReader : BasicJListCellReader(), JListCellReader {

    override fun valueAt(list: JList<*>, index: Int): String? {
        val element = list.model.getElementAt(index) ?: return null
        val cellRendererComponent = getListCellRendererComponent(list, element, index)
        return getValueWithCellRenderer(cellRendererComponent)
    }
}

@Deprecated("Will be replaced with external fixture lib")
class ExtendedJTableCellReader : BasicJTableCellReader(), JTableCellReader {

    override fun valueAt(table: JTable, row: Int, column: Int): String? {
        val cellRendererComponent = table.prepareRenderer(table.getCellRenderer(row, column), row, column)
        return getValueWithCellRenderer(cellRendererComponent)
    }
}

@Deprecated("Will be replaced with external fixture lib")
fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int): Component {
    return (list as JList<Any>).cellRenderer.getListCellRendererComponent(list, value, index, true, true)
}


fun <ReturnType> computeOnEdt(query: () -> ReturnType): ReturnType? = GuiActionRunner.execute(object : GuiQuery<ReturnType>() {
    override fun executeInEDT(): ReturnType = query()
})

@Deprecated("Will be replaced with external fixture lib")
fun getValueWithCellRenderer(cellRendererComponent: Component, isExtended: Boolean = true): String? {
    val result = when (cellRendererComponent) {
        is JLabel -> cellRendererComponent.text
        is NodeRenderer -> {
            if (isExtended) cellRendererComponent.getFullText()
            else cellRendererComponent.getFirstText()
        } //should stands before SimpleColoredComponent because it is more specific
        is SimpleColoredComponent -> cellRendererComponent.getFullText()
        is MultilineTreeCellRenderer -> cellRendererComponent.text
        else -> cellRendererComponent.findText()
    }
    return result?.trimEnd()
}

@Deprecated("Will be replaced with external fixture lib")
fun SimpleColoredComponent.getFullText(): String {
    return this.getCharSequence(false).toString()
}

@Deprecated("Will be replaced with external fixture lib")
fun SimpleColoredComponent.getFirstText(): String {
    return this.getCharSequence(true).toString()
}

@Deprecated("Will be replaced with external fixture lib")
fun Component.findText(): String? {
    try {
        assert(this is Container)
        val container = this as Container
        val resultList = ArrayList<String>()
        resultList.addAll(
                findAllWithBFS<JLabel>(container)
                        .asSequence()
                        .filter { !it.text.isNullOrEmpty() }
                        .map { it.text }
                        .toList()
        )
        resultList.addAll(
                findAllWithBFS<SimpleColoredComponent>(container)
                        .map { it.getFullText() }
                        .filter { it.isNotEmpty() }
                        .toList()
        )
        return resultList.firstOrNull { it.isNotEmpty() }
    }
    catch (ignored: ComponentLookupException) {
        return null
    }
}

inline fun <reified ComponentType : Component> findAllWithBFS(container: Container): List<ComponentType> {
    val result = LinkedList<ComponentType>()
    val queue: Queue<Component> = LinkedList()

    val check: (container: Component)-> Unit = {
        if (ComponentType::class.java.isInstance(it)) result.add(it as ComponentType)
    }

    queue.add(container)
    while (queue.isNotEmpty()) {
        val polled = queue.poll()
        check(polled)
        if (polled is Container)
            queue.addAll(polled.components)
    }
    return result
}
