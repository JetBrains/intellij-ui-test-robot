// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin;

import com.automation.remarks.junit5.Video;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.*;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import org.assertj.swing.core.MouseButton;
import org.intellij.examples.simple.plugin.pages.IdeaFrame;
import org.intellij.examples.simple.plugin.steps.JavaExampleSteps;
import org.intellij.examples.simple.plugin.utils.RemoteRobotExtension;
import org.intellij.examples.simple.plugin.utils.StepsLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.awt.event.KeyEvent.*;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.intellij.examples.simple.plugin.pages.ActionMenuFixtureKt.actionMenu;
import static org.intellij.examples.simple.plugin.pages.ActionMenuFixtureKt.actionMenuItem;

@ExtendWith(RemoteRobotExtension.class)
public class CreateCommandLineJavaTest {

    private final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
    private final JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
    private final Keyboard keyboard = new Keyboard(remoteRobot);

    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @AfterEach
    public void closeProject(final RemoteRobot remoteRobot) {
        step("Close the project", () -> {
            if (remoteRobot.isMac()) {
                keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
                keyboard.enterText("Close Project");
                keyboard.enter();
            } else {
                actionMenu(remoteRobot, "File").click();
                actionMenuItem(remoteRobot, "Close Project").click();
            }
        });
    }

    @Test
    @Video
    void createCommandLineProject(final RemoteRobot remoteRobot) {
        sharedSteps.createNewCommandLineProject();
        sharedSteps.closeTipOfTheDay();

        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("Create Java file", () -> {
            final ContainerFixture projectView = idea.getProjectViewTree();
            if (!projectView.hasText("src")) {
                projectView.findText(idea.getProjectName()).doubleClick();
                waitFor(() -> projectView.hasText("src"));
            }
            projectView.findText("src").click(MouseButton.RIGHT_BUTTON);
            actionMenu(remoteRobot, "New").click();
            actionMenuItem(remoteRobot, "Java Class").click();
            keyboard.enterText("App");
            keyboard.enter();
        });

        final TextEditorFixture editor = idea.textEditor(Duration.ofSeconds(2));

        step("Write a code", () -> {
            editor.getEditor().findText("App").click();
            sharedSteps.goToLineAndColumn(1, 19);
            keyboard.enter();
            sharedSteps.autocomplete("main");
            sharedSteps.autocomplete("sout");
            keyboard.enterText("\"");
            keyboard.enterText("Hello from UI test");
        });

        step("Launch the application", () -> {
            waitFor(Duration.ofSeconds(20), () -> !editor
                    .find(JButtonFixture.class, byXpath("//div[@class='TrafficLightButton']"))
                    .hasText("Analyzing..."));
            waitFor(Duration.ofSeconds(10), () -> editor.getGutter().getIcons().size() > 0);
            final GutterIcon runIcon = editor.getGutter().getIcons()
                    .stream()
                    .filter((it) -> it.getDescription().contains("run.svg"))
                    .findFirst()
                    .orElseThrow(() -> {
                        throw new IllegalStateException("No Run icon presents in the gutter");
                    });
            runIcon.click();
            idea.find(CommonContainerFixture.class, byXpath("//div[@class='HeavyWeightWindow']"), Duration.ofSeconds(4))
                    .button(byXpath("//div[@disabledicon='execute.svg']"), Duration.ofSeconds(4))
                    .click();
        });
        step("Check console output", () -> {
            final Locator locator = byXpath("//div[@class='ConsoleViewImpl']");
            waitFor(ofMinutes(1), () -> idea.findAll(ContainerFixture.class, locator).size() > 0);
            waitFor(ofMinutes(1), () -> idea.find(ComponentFixture.class, locator)
                    .hasText("Hello from UI test"));
        });
    }
}
