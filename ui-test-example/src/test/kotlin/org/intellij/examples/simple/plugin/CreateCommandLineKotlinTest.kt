// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.assertj.swing.core.MouseButton
import org.intellij.examples.simple.plugin.pages.*
import org.intellij.examples.simple.plugin.steps.JavaExampleSteps
import org.intellij.examples.simple.plugin.utils.RemoteRobotExtension
import org.intellij.examples.simple.plugin.utils.StepsLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.awt.event.KeyEvent.*
import java.time.Duration.ofMinutes

@ExtendWith(RemoteRobotExtension::class)
class CreateCommandLineKotlinTest {
    init {
        StepsLogger.init()
    }

    @AfterEach
    fun closeProject(remoteRobot: RemoteRobot) = with(remoteRobot) {
        idea {
            when {
                isMac() -> keyboard {
                    hotKey(VK_SHIFT, VK_META, VK_A)
                    enterText("Close Project")
                    enter()
                }
                else -> {
                    actionMenu("File").click()
                    actionMenuItem("Close Project").click()
                }
            }
        }
    }

    @Test
    fun createCommandLineApp(remoteRobot: RemoteRobot) = with(remoteRobot) {
        val sharedSteps = JavaExampleSteps(this)

        welcomeFrame {
            createNewProjectLink.click()
            dialog("New Project") {
                findText("Java").click()
                find(
                    ComponentFixture::class.java,
                    byXpath("//div[@class='FrameworksTree']")
                ).findText("Kotlin/JVM").click()
                runJs("robot.pressAndReleaseKey($VK_SPACE)")
                button("Next").click()
                button("Finish").click()
            }
        }
        sharedSteps.closeTipOfTheDay()
        idea {
            waitFor(ofMinutes(5)) { isDumbMode().not() }
            step("Create App file") {
                with(projectViewTree) {
                    if (hasText("src").not()) {
                        findText(projectName).doubleClick()
                        waitFor { hasText("src") }
                    }
                    findText("src").click(MouseButton.RIGHT_BUTTON)
                }
                actionMenu("New").click()
                actionMenuItem("Kotlin Class/File").click()
                keyboard { enterText("App"); down(); enter() }
            }
            with(textEditor()) {
                step("Write a code") {
                    click()
                    sharedSteps.autocomplete("main")
                    keyboard { enterText("println(\""); enterText("Hello from UI test") }
                }
                step("Launch application") {
                    gutter.getIcons().first { it.description.contains("run.svg") }.click()
                    this@idea.find<CommonContainerFixture>(
                        byXpath("//div[@class='HeavyWeightWindow']")
                    ).button(byXpath("//div[@disabledicon='execute.svg']"))
                        .click()
                }
            }

            val consoleLocator = byXpath("ConsoleViewImpl", "//div[@class='ConsoleViewImpl']")
            step("Wait for Console appears") {
                waitFor(ofMinutes(1)) { findAll<ContainerFixture>(consoleLocator).isNotEmpty() }
            }
            step("Check the message") {
                waitFor(ofMinutes(1)) { find<ContainerFixture>(consoleLocator).hasText("Hello from UI test") }
            }
        }
    }
}