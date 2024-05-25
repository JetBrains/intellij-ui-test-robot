// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin.steps;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.JListFixture;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import kotlin.Unit;
import org.intellij.examples.simple.plugin.pages.DialogFixture;
import org.intellij.examples.simple.plugin.pages.IdeaFrame;
import org.intellij.examples.simple.plugin.pages.WelcomeFrameFixture;

import java.awt.event.KeyEvent;
import java.time.Duration;

import static com.intellij.remoterobot.fixtures.dataExtractor.TextDataPredicatesKt.contains;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static com.intellij.remoterobot.utils.UtilsKt.hasSingleComponent;
import static java.time.Duration.ofSeconds;
import static org.intellij.examples.simple.plugin.pages.DialogFixture.byTitle;

public class JavaExampleSteps {
    final private RemoteRobot remoteRobot;
    final private Keyboard keyboard;

    public JavaExampleSteps(RemoteRobot remoteRobot) {
        this.remoteRobot = remoteRobot;
        this.keyboard = new Keyboard(remoteRobot);
    }

    public void createNewCommandLineProject() {
        step("Create New Project", () -> {
            final WelcomeFrameFixture welcomeFrame = remoteRobot.find(WelcomeFrameFixture.class, Duration.ofSeconds(10));
            welcomeFrame.createNewProjectLink().click();

            final DialogFixture newProjectDialog = welcomeFrame.find(DialogFixture.class, DialogFixture.byTitle("New Project"), Duration.ofSeconds(20));
            newProjectDialog.findText("Java").click();
            newProjectDialog.button("Create").click();
        });
    }

    public void closeTipOfTheDay() {
        step("Close Tip of the Day if it appears", () -> {
            waitFor(Duration.ofSeconds(20), () -> remoteRobot.findAll(DialogFixture.class, byXpath("//div[@class='MyDialog'][.//div[@text='Running startup activities...']]")).size() == 0);
            final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
            idea.dumbAware(() -> {
                try {
                    idea.find(DialogFixture.class, byTitle("Tip of the Day")).button("Close").click();
                } catch (Throwable ignore) {
                }
                return Unit.INSTANCE;
            });
        });
    }

    public void autocomplete(String text) {
        step("Autocomplete '" + text + "'", () -> {
            final Locator completionMenu = byXpath("//div[@class='HeavyWeightWindow']//div[@class='LookupList']");
            final Keyboard keyboard = new Keyboard(remoteRobot);
            keyboard.enterText(text);
            waitFor(ofSeconds(10), () -> hasSingleComponent(remoteRobot, completionMenu));
            remoteRobot.find(ComponentFixture.class, completionMenu)
                    .findText(contains(text))
                    .click();
            keyboard.enter();
        });
    }

    public void goToLineAndColumn(int row, int column) {
        if (remoteRobot.isMac())
            keyboard.hotKey(KeyEvent.VK_META, KeyEvent.VK_L);
        else
            keyboard.hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_G);
        keyboard.enterText(row + ":" + column);
        keyboard.enter();
    }
}
