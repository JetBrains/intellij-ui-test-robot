package com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ContainerFixture

class ListTextFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(
    remoteRobot,
    remoteComponent
) {
    init {
        runJs(
            """
            const fixture = JListFixture(robot, component);
            const cellReader = new com.intellij.remoterobot.fixtures.dataExtractor.server.textCellRenderers.JListTextCellReader();
            fixture.replaceCellReader(cellReader);
            ctx.put("fixture", fixture) 
        """
        )
    }

    fun list(): List<String> {
        return callJs<Array<String>>(
            """
           ctx.get("fixture").contents();
        """
        ).toList()
    }
}