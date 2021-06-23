package org.intellij.examples.simple.plugin

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.byXpath
import org.intellij.examples.simple.plugin.utils.RemoteRobotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RemoteRobotExtension::class)
class GlobalAndLocalMapExamplesTest {

    @Test
    fun globalMap(remoteRobot: RemoteRobot) = with(remoteRobot) {
        // Save message
        runJs("global.put('greeting', 'Hello from Idea')")

        // Get message with other request
        assert(callJs<String>("global.get('greeting')") == "Hello from Idea")
    }

    @Test
    fun storeFunctions(remoteRobot: RemoteRobot) = with(remoteRobot) {
        // Save JS object with functions
        runJs(
            """
           importPackage(com.intellij.openapi.application)
           const steps = {
                applicationInfo: function() {
                    return ApplicationInfo.getInstance()
                },
                majorVersion: () => steps.applicationInfo().getMajorVersion(),
                minorVersion: () => steps.applicationInfo().getMinorVersion(),
                buildNumber: () => steps.applicationInfo().getBuildNumber(),
                getMyInfo: () => 'My Idea version: ' + steps.majorVersion() + '.' + steps.minorVersion() + ' - ' + steps.buildNumber()
           }
           global.put('steps', steps)
        """
        )
        // Call a function
        val version = callJs<String>("global.get('steps').getMyInfo()")
        println(version)
    }

    @Test
    fun local(remoteRobot: RemoteRobot) {
        // local map is unique for each fixture even if two fixtures refers to the same component
        val frame1 = remoteRobot.find<FrameFixture>(byXpath("//div[@class='FlatWelcomeFrame']"))
        val frame2 = remoteRobot.find<FrameFixture>(byXpath("//div[@class='FlatWelcomeFrame']"))
        assert(frame1.id != frame2.id)
    }

    class FrameFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
        ContainerFixture(remoteRobot, remoteComponent) {
        init {
            runJs("local.put('id', java.util.UUID.randomUUID().toString())")
        }

        val id: String
            get() = callJs("local.get('id')")
    }
}