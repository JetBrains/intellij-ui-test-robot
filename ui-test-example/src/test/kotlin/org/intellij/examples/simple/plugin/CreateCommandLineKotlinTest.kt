// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin

import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.autocomplete
import com.intellij.remoterobot.utils.keyboard
import com.intellij.remoterobot.utils.waitFor
import org.assertj.swing.core.MouseButton
import org.intellij.examples.simple.plugin.pages.*
import org.intellij.examples.simple.plugin.steps.JavaExampleSteps
import org.intellij.examples.simple.plugin.utils.StepsLogger
import org.intellij.examples.simple.plugin.utils.uiTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.awt.event.KeyEvent.*
import java.time.Duration

class CreateCommandLineKotlinTest {
    init {
        StepsLogger.init()
    }

    @AfterEach
    fun closeProject() = uiTest {
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
    fun createCommandLineApp() = uiTest {
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
            step("Create App file") {
                with(projectViewTree) {
                    findText(projectName).doubleClick()
                    waitFor { hasText("src") }
                    findText("src").click(MouseButton.RIGHT_BUTTON)
                }
                actionMenu("New").click()
                actionMenuItem("Kotlin Class/File").click()
                keyboard { enterText("App"); down(); enter() }
            }
            editor("App.kt") {
                step("Write a code") {
                    autocomplete("main")
                    keyboard { enterText("println(\""); enterText("Hello from UI test") }
                }
                step("Launch application") {
                    findText("main").click()
                    keyboard { hotKey(VK_ALT, VK_ENTER); enter() }
                }
            }

            val consoleLocator = byXpath("ConsoleViewImpl", "//div[@class='ConsoleViewImpl']")
            step("Wait for Console appears") {
                waitFor(Duration.ofMinutes(1)) { findAll<ContainerFixture>(consoleLocator).isNotEmpty() }
            }
            step("Check the message") {
                assert(find<ContainerFixture>(consoleLocator).hasText("Hello from UI test"))
            }
        }
    }
}