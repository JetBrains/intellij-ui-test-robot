// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.examples.simple.plugin;

import com.intellij.remoterobot.RemoteRobot;
import org.intellij.examples.simple.plugin.pages.WelcomeFrame;
import org.intellij.examples.simple.plugin.utils.StepsLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.intellij.remoterobot.fixtures.dataExtractor.TextDataPredicatesKt.startsWith;


public class SayHelloJavaTest {
    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @Test
    void checkSayHello() {
        final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
        final WelcomeFrame welcomeFrame = remoteRobot.find(WelcomeFrame.class, Duration.ofSeconds(10));
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));
        if (!welcomeFrame.hasText("Say Hello")) {
            welcomeFrame.getMoreActions().click();
            welcomeFrame.getHeavyWeightPopup().findText("Say Hello").click();
        } else {
            welcomeFrame.findText("Say Hello").click();
        }
        final SayHelloKotlinTest.HelloWorldDialog helloDialog = remoteRobot.find(SayHelloKotlinTest.HelloWorldDialog.class);
        assert (helloDialog.getTextPane().hasText("Hello World!"));
        helloDialog.getOk().click();
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));
    }
}
