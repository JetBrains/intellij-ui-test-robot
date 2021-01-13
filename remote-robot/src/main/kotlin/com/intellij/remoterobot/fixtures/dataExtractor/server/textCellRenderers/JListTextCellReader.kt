package com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers

import com.intellij.remoterobot.fixtures.dataExtractor.server.TextParser
import com.intellij.remoterobot.fixtures.dataExtractor.server.computeOnEdt
import org.assertj.swing.cell.JListCellReader
import java.awt.Dimension
import javax.swing.JList
import javax.swing.ListCellRenderer

class JListTextCellReader: JListCellReader {
    override fun valueAt(list: JList<*>?, index: Int): String? {
        return computeOnEdt {
            require(list != null)
            val item = list.model.getElementAt(index)
            val renderer = list.cellRenderer as ListCellRenderer<Any>
            val c = renderer.getListCellRendererComponent(JList(), item, index, true, true)
            // fake size to make it paintable
            c.size = Dimension(list.width, 100)
            TextParser.parseCellRenderer(c, true).joinToString(" ") { it.trim() }
        }
    }
}