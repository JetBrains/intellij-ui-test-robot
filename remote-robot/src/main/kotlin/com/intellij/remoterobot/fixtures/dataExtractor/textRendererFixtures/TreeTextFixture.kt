package com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ContainerFixture
import java.util.*

class TreeTextFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(
    remoteRobot,
    remoteComponent
)  {
    init {
        runJs(
            """
            const fixture = JTreeFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JTreeTextCellReader();
            fixture.replaceCellReader(cellReader);
            fixture.replaceSeparator("|")
            ctx.put("fixture", fixture) 
        """
        )
    }
    fun valueAt(row: Int): String {
        return callJs<String>(
            """
           ctx.get("fixture").valueAt($row);
        """
        )
    }
    fun clickPath(path: List<String>) {
        expandPath(path)
        runJs("""
           ctx.get("fixture").clickPath("${path.joinToString("|") { it }}")
        """)
    }
    fun expandPath(path: List<String>) {
        val pathBuilder = StringJoiner("|")
        path.forEach {
            pathBuilder.add(it)
            runJs(
                """
           ctx.get("fixture").expandPath("${pathBuilder.toString()}")
        """
            )
        }
    }
}