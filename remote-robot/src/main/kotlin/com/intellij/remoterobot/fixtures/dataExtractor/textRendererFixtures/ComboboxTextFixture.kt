package com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ContainerFixture

class ComboboxTextFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(
    remoteRobot,
    remoteComponent
) {
    init {
        runJs(
            """
            const fixture = JComboBoxFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JComboBoxTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("fixture", fixture) 
        """
        )
    }

    fun selectedText(): String {
        return callJs("""
           ctx.get("fixture").selectedItem() 
        """)
    }

    fun list(): List<String> {
        return callJs<Array<String>>(
            """
           ctx.get("fixture").contents();
        """
        ).toList()
    }
}