// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.dataExtractor

import org.assertj.swing.cell.JTreeCellReader
import org.assertj.swing.driver.BasicJTreeCellReader
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.edt.GuiQuery
import java.awt.Dimension
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class JTreeTextCellReader : BasicJTreeCellReader(), JTreeCellReader {
    override fun valueAt(tree: JTree, modelValue: Any?): String? {
        if (modelValue == null) return null
        val isLeaf = modelValue is DefaultMutableTreeNode && modelValue.isLeaf
        return computeOnEdt {
            val cellRendererComponent =
                tree.cellRenderer.getTreeCellRendererComponent(tree, modelValue, false, false, isLeaf, 0, false)

            // fake size to make it paintable
            cellRendererComponent.size = Dimension(100, 100)

            TextParser.parseCellRenderer(cellRendererComponent, true).joinToString(" ") { it.trim() }
        }
    }
}
fun <ReturnType> computeOnEdt(query: () -> ReturnType): ReturnType? = GuiActionRunner.execute(object : GuiQuery<ReturnType>() {
    override fun executeInEDT(): ReturnType = query()
})