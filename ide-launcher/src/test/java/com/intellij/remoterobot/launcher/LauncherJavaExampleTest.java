package com.intellij.remoterobot.launcher;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JLabelFixture;
import okhttp3.OkHttpClient;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.intellij.remoterobot.launcher.RemoteRobotExtKt.isAvailable;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LauncherExampleTest.IdeTestWatcher.class)
@Timeout(value = 25, unit = TimeUnit.MINUTES)
public class LauncherJavaExampleTest {
    private static Process ideaProcess;
    private static Path tmpDir;
    private static RemoteRobot remoteRobot;
    private final static Ide.BuildType buildType = Ide.BuildType.RELEASE;
    private final static String version = "2021.3.2";
    static {
        try {
            tmpDir = Files.createTempDirectory("launcher");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeAll
    public static void before() {
        final OkHttpClient client = new OkHttpClient();
        remoteRobot = new RemoteRobot("http://localhost:8082", client);
        final IdeDownloader ideDownloader = new IdeDownloader(client);
        final Map<String, Object> additionalProperties = new HashMap<>();
        additionalProperties.put("robot-server.port", 8082);
        final List<Path> plugins = new ArrayList<>();
        plugins.add(ideDownloader.downloadRobotPlugin(tmpDir));
        // plugins.add(path to your plugin)
        ideaProcess = IdeLauncher.INSTANCE.launchIde(
                ideDownloader.downloadAndExtract(Ide.IDEA_COMMUNITY, tmpDir, buildType, version),
                additionalProperties,
                Collections.emptyList(),
                plugins,
                tmpDir
        );
        waitFor(Duration.ofSeconds(90), Duration.ofSeconds(5), () -> isAvailable(remoteRobot));
    }

    @AfterAll
    public static void after() throws IOException {
        ideaProcess.destroy();
        FileUtils.cleanDirectory(tmpDir.toFile());
    }

    @Test
    public void test() {
        final JLabelFixture welcomeLabel = remoteRobot.find(JLabelFixture.class, byXpath("//div[@text.key='label.welcome.to.0']"));
        assertEquals(welcomeLabel.getValue(), "Welcome to IntelliJ IDEA");
    }
}
