package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import javax.swing.JTree

open class JTreeFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JTree::class.java)
    }

    init {
        runJs(
            """
            const fixture = new JTreeFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JTreeTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("fixture", fixture);
            ctx.put("reader", cellReader)
        """
        )
    }

    fun clickPath(vararg path: String, fullMatch: Boolean = true) {
        findExpandedPath(*path, fullMatch = fullMatch)?.let {
            clickRow(it.row)
        } ?: throw PathNotFoundException(path.toList())
    }

    fun doubleClickPath(vararg path: String, fullMatch: Boolean = true) {
        findExpandedPath(*path, fullMatch = fullMatch)?.let {
            doubleClickRow(it.row)
        } ?: throw PathNotFoundException(path.toList())
    }

    fun rightClickPath(vararg path: String, fullMatch: Boolean = true) {
        findExpandedPath(*path, fullMatch = fullMatch)?.let {
            rightClickRow(it.row)
        } ?: throw PathNotFoundException(path.toList())
    }

    fun clickRowWithText(text: String, fullMatch: Boolean = true) {
        findExpandedRowWithText(text, fullMatch)?.let {
            clickRow(it.row)
        } ?: throw PathNotFoundException()
    }

    fun doubleClickRowWithText(text: String, fullMatch: Boolean = true) {
        findExpandedRowWithText(text, fullMatch)?.let {
            doubleClickRow(it.row)
        } ?: throw PathNotFoundException()
    }

    fun rightClickRowWithText(text: String, fullMatch: Boolean = true) {
        findExpandedRowWithText(text, fullMatch)?.let {
            rightClickRow(it.row)
        } ?: throw PathNotFoundException()
    }

    fun clickRow(rowNumber: Int) {
        runJs("""
           ctx.get("fixture").clickRow($rowNumber) 
        """)
    }

    fun doubleClickRow(rowNumber: Int) {
        runJs("""
           ctx.get("fixture").doubleClickRow($rowNumber) 
        """)
    }

    fun rightClickRow(rowNumber: Int) {
        runJs("""
           ctx.get("fixture").rightClickRow($rowNumber) 
        """)
    }

    fun collectExpandedPaths(): List<TreePathToRow> = callJs<ArrayList<ArrayList<String?>>>("""
            const paths = new java.util.ArrayList()
            const cellReader = ctx.get("reader")
            com.intellij.util.ui.tree.TreeUtil.visitVisibleRows(component, (p) => {
                const nodes = new java.util.ArrayList()
                for (let node of p.getPath()) {
                    nodes.add(cellReader.valueAt(component, node))
                }
                
                // If the root node is not visible, remove it
                if (component.isRootVisible() === false)
                    nodes.remove(0)
                return nodes
            }, (p) => paths.add(p))
            paths
        """, true).mapIndexed { index, path ->
        TreePathToRow(path.filterNotNull().filter { it.isNotEmpty() }, index)
    }

    fun collectSelectedPaths(): List<List<String>> = callJs<ArrayList<ArrayList<String?>>>("""
        const paths = new java.util.ArrayList()
        const treePaths = component.getSelectionPaths()
        const cellReader = ctx.get("reader")

        if (treePaths) {
            for (let i = 0; i < treePaths.length; ++i) {
                const nodes = new java.util.ArrayList()
                for (let node of treePaths[i].getPath()) {
                    nodes.add(cellReader.valueAt(component, node))
                }
                
                // If the root node is not visible, remove it
                if (component.isRootVisible() === false)
                    nodes.remove(0)
                paths.add(nodes)
            }
        }
        paths
    """, true).map { path ->
        path.filterNotNull().filter { it.isNotEmpty() }
    }

    fun collectRows(): List<String> = collectExpandedPaths().map { it.path.last() }

    fun isPathExists(vararg path: String, fullMatch: Boolean = true) = findExpandedPath(*path, fullMatch = fullMatch) != null

    fun isPathSelected(vararg path: String) = collectSelectedPaths().contains(path.toList())

    fun getValueAtRow(row: Int) = collectRows()[row]

    fun expand(vararg path: String): JTreeFixture {
        runJs("""
            const expandingPathNodes = [${path.joinToString(",") { "\"${it}\"" }}]
            const ignoreRoot = component.isRootVisible() === false
            const treePath = com.intellij.ui.tree.TreePathUtil.convertArrayToTreePath(expandingPathNodes)
            const toStringConverter = function(obj) {return java.util.Objects.toString(obj)}
            const visitor = new com.intellij.ui.tree.TreeVisitor.ByTreePath(ignoreRoot, treePath, toStringConverter);
            
            com.intellij.util.ui.tree.TreeUtil.promiseExpand(component, visitor).blockingGet(5000)
        """)
        return this
    }

    fun expandAll(): JTreeFixture {
        step("Expand all") {
            runJs("com.intellij.util.ui.tree.TreeUtil.promiseExpandAll(component).blockingGet(5000)")
        }
        return this
    }

    fun expandAllExcept(vararg nodes: String): JTreeFixture {
        runJs("""
            var excludedNodes = [${nodes.joinToString(",") { "\"$it\"" }}]
            function visit(treePath) {
                var pathStr = treePath.toString()
                if (excludedNodes.some(function(node) { return pathStr.indexOf(node) !== -1 })) {
                    return com.intellij.ui.tree.TreeVisitor.Action.SKIP_CHILDREN
                } else {
                    return com.intellij.ui.tree.TreeVisitor.Action.CONTINUE
                }
            }
            const visitor = { visit: visit }
            
            com.intellij.util.ui.tree.TreeUtil.promiseExpand(component, new com.intellij.ui.tree.TreeVisitor(visitor)).blockingGet(5000)
        """)
        return this
    }

    fun collapsePath(vararg path: String, fullMatch: Boolean = true) = findExpandedPath(*path, fullMatch = fullMatch)?.let {
        runJs("""
            ctx.get("fixture").collapseRow(${it.row})
        """)
    }

    private fun findExpandedPath(vararg path: String, fullMatch: Boolean): TreePathToRow? = collectExpandedPaths().singleOrNull { expandedPath ->
        expandedPath.path.size == path.size && expandedPath.path.containsAllNodes(*path, fullMatch = fullMatch) ||
                expandedPath.path.size - 1 == path.size && expandedPath.path.drop(1).containsAllNodes(*path, fullMatch = fullMatch)
    }

    private fun findExpandedRowWithText(text: String, fullMatch: Boolean = true): TreePathToRow? = collectExpandedPaths().singleOrNull { expandedPath ->
        if (fullMatch) expandedPath.path.last() == text else expandedPath.path.last().contains(text, true)
    }

    private fun List<String>.containsAllNodes(vararg treePath: String, fullMatch: Boolean): Boolean = zip(treePath).all {
        if (fullMatch) {
            it.first.equals(it.second, true)
        } else {
            it.first.contains(it.second, true)
        }
    }

    data class TreePathToRow(val path: List<String>, val row: Int)

    class PathNotFoundException(message: String? = null) : Exception(message) {
        constructor(path: List<String>) : this("$path not found")
    }
}
