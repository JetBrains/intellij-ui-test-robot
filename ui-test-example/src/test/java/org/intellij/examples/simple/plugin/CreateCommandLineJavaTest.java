// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.ContainerFixture;
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
import static com.intellij.remoterobot.utils.KeyboardUtilsKt.autocomplete;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.awt.event.KeyEvent.*;
import static java.time.Duration.ofMinutes;
import static org.intellij.examples.simple.plugin.pages.ActionMenuFixtureKt.actionMenu;
import static org.intellij.examples.simple.plugin.pages.ActionMenuFixtureKt.actionMenuItem;
import static org.intellij.examples.simple.plugin.pages.EditorKt.editor;

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
    void createCommandLineProject(final RemoteRobot remoteRobot) {
        sharedSteps.createNewCommandLineProject();
        sharedSteps.closeTipOfTheDay();

        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class);
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("Create New Kotlin file", () -> {
            final ContainerFixture projectView = idea.getProjectViewTree();

            projectView.findText(idea.getProjectName()).doubleClick();
            waitFor(() -> projectView.hasText("src"));
            projectView.findText("src").click(MouseButton.RIGHT_BUTTON);
            actionMenu(remoteRobot, "New").click();
            actionMenuItem(remoteRobot, "Kotlin Class/File").click();
            keyboard.enterText("App");
            keyboard.down();
            keyboard.enter();
        });

        final ContainerFixture editor = editor(idea, "App.kt");

        step("Write a code", () -> {
            autocomplete(remoteRobot, "main");
            keyboard.enterText("println(\"");
            keyboard.enterText("Hello from UI test");
        });

        step("Launch the application", () -> {
            editor.findText("main").click(MouseButton.RIGHT_BUTTON);
            idea.find(ComponentFixture.class,
                    byXpath("//div[@class='ActionMenuItem' and contains(@text, 'Run')]")
            ).click();
        });
        step("Check console output", () -> {
            final Locator locator = byXpath("//div[@class='ConsoleViewImpl']");
            waitFor(ofMinutes(1), () -> idea.findAll(ContainerFixture.class, locator).size() > 0);
            waitFor(ofMinutes(1), () -> idea.find(ComponentFixture.class, locator)
                    .hasText("Hello from UI test"));
        });
    }
}
