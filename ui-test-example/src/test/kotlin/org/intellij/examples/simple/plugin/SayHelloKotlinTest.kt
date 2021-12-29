// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin

import com.automation.remarks.junit5.Video
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.launcher.Ide
import com.intellij.remoterobot.launcher.IdeDownloader
import com.intellij.remoterobot.launcher.IdeLauncher
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.utils.waitFor
import org.intellij.examples.simple.plugin.pages.WelcomeFrame
import org.intellij.examples.simple.plugin.utils.RemoteRobotExtension
import org.intellij.examples.simple.plugin.utils.StepsLogger
import org.intellij.examples.simple.plugin.utils.isAvailable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration

@ExtendWith(RemoteRobotExtension::class)
class SayHelloKotlinTest {

    companion object {
        private var ideaProcess: Process? = null

        @BeforeAll
        @JvmStatic
        fun startIdea(remoteRobot: RemoteRobot, @TempDir tmpDir: Path) {
            val ideDownloader = IdeDownloader()
            ideaProcess = IdeLauncher.launchIde(
                ideDownloader.downloadAndExtractLatestEap(Ide.IDEA_COMMUNITY, tmpDir),
                mapOf("robot-server.port" to 8082),
                emptyList(),
                listOf(ideDownloader.downloadRobotPlugin(tmpDir), Paths.get("build", "distributions", "ui-test-example.zip")),
                tmpDir
            )
            waitFor(Duration.ofMinutes(1), Duration.ofSeconds(5)) {
                remoteRobot.isAvailable()
            }
        }

        @AfterAll
        @JvmStatic
        fun killIdeaProcess() {
            ideaProcess?.destroyForcibly()
        }
    }

    init {
        StepsLogger.init()
    }

    @Test
    @Video
    fun checkHelloMessage(remoteRobot: RemoteRobot) = with(remoteRobot) {
        find(WelcomeFrame::class.java, timeout = Duration.ofSeconds(10)).apply {
            if (hasText("Say Hello")) {
                findText("Say Hello").click()
            } else {
                moreActions.click()
                heavyWeightPopup.findText("Say Hello").click()
            }
        }

        val helloDialog = find(HelloWorldDialog::class.java)

        assert(helloDialog.textPane.hasText("Hello World!"))
        helloDialog.ok.click()
    }

    @DefaultXpath("title Hello", "//div[@title='Hello' and @class='MyDialog']")
    class HelloWorldDialog(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(remoteRobot, remoteComponent) {
        val textPane: ComponentFixture
            get() = find(byXpath("//div[@class='Wrapper']//div[@class='JTextPane']"))
        val ok: ComponentFixture
            get() = find(byXpath("//div[@class='JButton' and @text='OK']"))
    }
}