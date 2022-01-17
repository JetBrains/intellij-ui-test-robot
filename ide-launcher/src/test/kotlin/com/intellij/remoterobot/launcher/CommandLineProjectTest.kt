package com.intellij.remoterobot.launcher

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.StepLogger
import com.intellij.remoterobot.stepsProcessing.StepWorker
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.hasSingleComponent
import com.intellij.remoterobot.utils.waitFor
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.JDialog

@ExtendWith(CommandLineProjectTest.IdeTestWatcher::class)
@Timeout(value = 25, unit = TimeUnit.MINUTES)
class CommandLineProjectTest {
    companion object {
        private var ideaProcess: Process? = null
        private var tmpDir: Path = Files.createTempDirectory("launcher")
        private lateinit var remoteRobot: RemoteRobot

        @BeforeAll
        @JvmStatic
        fun startIdea() {
            StepWorker.registerProcessor(StepLogger())
            
            val client = OkHttpClient()
            remoteRobot = RemoteRobot("http://localhost:8082", client)
            val ideDownloader = IdeDownloader(client)
            ideaProcess = IdeLauncher.launchIde(
                ideDownloader.downloadAndExtractLatestEap(Ide.IDEA_COMMUNITY, tmpDir),
                mapOf("robot-server.port" to 8082),
                emptyList(),
                listOf(ideDownloader.downloadRobotPlugin(tmpDir)),
                tmpDir
            )
            waitFor(Duration.ofSeconds(90), Duration.ofSeconds(5)) {
                remoteRobot.isAvailable()
            }
        }

        @AfterAll
        @JvmStatic
        fun cleanUp() {
            ideaProcess?.destroy()
            tmpDir.toFile().deleteRecursively()
        }
    }

    @Test
    fun commandLineProjectTest() {
        remoteRobot.find<CommonContainerFixture>(
            Locators.byProperties(Locators.XpathProperty.SIMPLE_CLASS_NAME to "FlatWelcomeFrame"),
            Duration.ofSeconds(20)
        ).button(byXpath("""//div[contains(@defaulticon, 'createNewProject') or (@accessiblename='New Project' and @class='JBOptionButton')]"""))
            .click()
        remoteRobot.find<CommonContainerFixture>(Locators.byTypeAndProperties(JDialog::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to "New Project"), Duration.ofSeconds(10)).run {
            jList().clickItem("Java")
            button("Next").click()
            checkBox("Create project from template").select()
            jList().clickItem("Command Line App")
            button("Next").click()
            button("Finish").click()
        }
        remoteRobot.find<CommonContainerFixture>(Locators.byProperties(Locators.XpathProperty.SIMPLE_CLASS_NAME to "IdeFrameImpl"), Duration.ofSeconds(20)).run {
            find<CommonContainerFixture>(Locators.byTypeAndProperties(JDialog::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to "Tip of the Day"), Duration.ofSeconds(20))
                .button("Close").click()
            runCatching { button("Got It").click() }
            find<CommonContainerFixture>(Locators.byType(Box::class.java))
                .find<ComponentFixture>(Locators.byProperties(Locators.XpathProperty.TOOLTIP to "Run 'Main'"))
                .click()
            waitFor(Duration.ofSeconds(60)) {
                hasSingleComponent(Locators.byPropertiesContains(Locators.XpathProperty.TEXT to "Process finished with exit code 0"))
            }
        }
    }

    class IdeTestWatcher: TestWatcher {
        override fun testFailed(context: ExtensionContext, cause: Throwable?) {
            ImageIO.write(remoteRobot.getScreenshot(), "png", File("build/reports", "${context.displayName}.png"))
        }
    }
}