// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.search.locators.byXpath
import org.intellij.examples.simple.plugin.pages.WelcomeFrame
import org.intellij.examples.simple.plugin.utils.RemoteRobotExtension
import org.intellij.examples.simple.plugin.utils.StepsLogger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(RemoteRobotExtension::class)
class SayHelloKotlinTest {
    init {
        StepsLogger.init()
    }

    @Test
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