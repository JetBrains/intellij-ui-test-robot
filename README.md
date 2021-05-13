![official JetBrains project](https://jb.gg/badges/official.svg)

This library allows you to write and execute UI tests among IntelliJ Idea. You can test your Plugin.

## Quick Start
First we need to launch the IDE. Because the `runIdeForUiTests` task is blocking, we can run it as an asynchronous process:

`./gradlew ui-test-example:clean ui-test-example:runIdeForUiTests &`
 
Next, we can start the tests. Because they run locally, you must be sure the Welcome Frame is visible on the screen: 

`./gradlew ui-test-example:test`

Or, just run all tasks together with one command:

`./gradlew ui-test-example:clean ui-test-example:runIdeForUiTests & ./gradlew ui-test-example:test`


## Remote-Robot
The Remote-Robot library is inspired by Selenium WebDriver. It supports IntelliJ IDEA since version `2018.3`.

![](docs/simple-schema.png)

It consists of a `remote-robot` client and a `robot-server` plugin:
* `remote-robot` - is a client (test) side library used to send commands to the `robot-server` plugin. 
* `robot-server` - is an IDEA plugin that should run with the plugin you are developing. 

The easiest way to start the test system is to execute the `runIdeForUiTests` task. (See Quick Start section, above.) When IDEA is initialized, the `robot-server` plugin starts listening for commands from the UI test client.

The `remote-robot` library communicates with the `robot-server` plugin via HTTP protocol. This connection means you can launch IDEA on remote machines or in docker containers to check your plugin within different test environments.

### Setup
Last version of the Remote-Robot is `0.11.3`

In the test project:
```groovy
repositories {
    maven { url = "https://packages.jetbrains.team/maven/p/ij/intellij-dependencies" }
}
dependencies {
    testImplementation("com.intellij.remoterobot:remote-robot:REMOTE-ROBOT_VERSION")
}
```
In the plugin project:
```groovy
downloadRobotServerPlugin.version = REMOTER-ROBOT_VERSION

runIdeForUiTests {
    systemProperty "robot-server.port", "8082" // default port 8580
}
```
By default, the port is local, so it could not be reached from another host. 
In case you need make it public you can add system property in the `runIdeForUiTests` task:
```groovy
runIdeForUiTests {
    // ......
    systemProperty "robot-server.host.public", "true" // port is public
}
```

Of course, you can write UI tests in the plugin project. 

### Create RemoteRobot
In the UI test project:
```java
RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
```

### Searching Components
We use the [`XPath`](https://www.w3.org/TR/xpath-21/) query language to find components.
Once IDEA with `robot-server` has started, you can open `http://ROBOT-SERVER:PORT` [link](http://127.0.0.1:8082).
The page shows the IDEA UI components hierarchy in HTML format. You can find the component of interest and write an XPath to it, similar to Selenium WebDriver.
There is also a simple XPath generator, which can help write and test your XPaths.
![](docs/hierarchy.gif)

For example:
Define a locator:
```java
Locator loginToGitHubLocator = byXpath("//div[@class='MainButton' and @text='Log in to GitHub...']");
```
Find one component:
```java
ComponentFixture loginToGitHub = remoteRobot.find(ComponentFixture.class, loginToGitHubLocator);
```
Find many components:
```java
List<ContainterFixture> dialogs = remoteRobot.findAll(
    ComponentFixture.class, 
    byXpath("//div[@class='MyDialog']")
);
```

### Fixtures
Fixtures support the `PageObject` pattern. 
There are two basic fixtures:
- `ComponentFixture` is the simplest representation of a real any component with basic methods;
- `ContainerFixture` extends `ComponentFixture` and allows searching other components within it. 

You can create your own fixtures:
```java
@DefaultXpath(by = "FlatWelcomeFrame type", xpath = "//div[@class='FlatWelcomeFrame']")
@FixtureName(name = "Welcome Frame")
public class WelcomeFrameFixture extends ContainerFixture {
    public WelcomeFrameFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    // Create New Project 
    public ComponentFixture createNewProjectLink() {
        return find(ComponentFixture.class, byXpath("//div[@text='Create New Project' and @class='ActionLink']"));
    }

    // Import Project
    public ComponentFixture importProjectLink() {
        return find(ComponentFixture.class, byXpath("//div[@text='Import Project' and @class='ActionLink']"));
    }
}
```
```java
// find the custom fixture by its default xpath
WelcomeFrameFixture welcomeFrame = remoteRobot.find(WelcomeFrameFixture.class);
welcomeFrame.createNewProjectLink().click();
```
### Remote-Fixtures
We have prepared some basic fixtures:
```groovy
dependencies {
    testImplementation("com.intellij.remoterobot:remote-fixtures:REMOTE-ROBOT_VERSION")
}
```
The library contains Fixtures for most basic UI components. Please check [this package](https://github.com/JetBrains/intellij-ui-test-robot/tree/master/remote-fixtures/src/main/kotlin/com/intellij/remoterobot/fixtures) to learn more.
In case you want to add missing basic Fixtures you are welcome to PR or create an [issue](https://github.com/JetBrains/intellij-ui-test-robot/issues)
### Getting Data From a Real Component
We use the JavaScript [`rhino`](https://github.com/mozilla/rhino) engine to work with components on the IDEA side.

For example, retrieving text from ActionLink component:
```java
public class ActionLinkFixture extends ComponentFixture {
    public ActionLinkFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }
    
    public String text() {
        return callJs("component.getText();");
    }
}
```
We can retrieve data using `RemoteRobot` with the `callJs` method. In this case there is a `robot` var in the context of JavaScript execution. 
The `robot` is an instance of extending the [`org.assertj.swing.core.Robot`](https://joel-costigliola.github.io/assertj/swing/api/org/assertj/swing/core/Robot.html) class.

When you use the `callJs()` method of a `fixture` object, the `component` argument represents the actual UI component that was found (see Searching Components) and used to initialize the `ComponentFixture`.

The `runJs` method works the same way without any return value:
```java
public void click() {
        runJs("const offset = component.getHeight()/2;" +
                "robot.click(" +
                "component, " +
                "new Point(offset, offset), " +
                "MouseButton.LEFT_BUTTON, 1);"
        );
    }
```

We import some packages to the context before the script is executed:
```java
    java.awt
    org.assertj.swing.core
    org.assertj.swing.fixture
```
You can add other packages or classes with js [methods](https://www-archive.mozilla.org/rhino/apidocs/org/mozilla/javascript/importertoplevel):
```java
    importClass(java.io.File);            
    importPackage(java.io);
```
Or just use the full path:

```java
    Boolean isDumbMode = ideaFtame.callJs(
        "com.intellij.openapi.project.DumbService.isDumb(component.project);"
    );
```
In case you made robot-server-plugin port public, you may want to enable encryption for JavaScript code:
```groovy
runIdeForUiTests {
    systemProperty "robot.encryption.enabled", "true"
    systemProperty "robot.encryption.password", "secret"
}

test {
    systemProperty "robot.encryption.password", "secret"
}
```
### Text
Sometimes you may not want to dig through the whole component to find out which field contains the text you need to reach. 
If you need to check whether some text is present on the component, or you need to click at the text, 
you can use `fixture` methods:
```java
welcomeFrame.findText("Create New Project").click();

assert(welcomeFrame.hasText(startsWith("Version 20")));

List<String> renderedText = welcomeFrame.findAllText()
    .stream()
    .map(RemoteText::getText)
    .collect(Collectors.toList());
```
Instead of looking for text inside the component structure we just render it on a fake `Graphics` to collect text data and its points.

### Kotlin
If you already familiar with Kotlin, please take a look at the [kotlin example](/ui-test-example/src/test/kotlin/org/intellij/examples/simple/plugin/CreateCommandLineKotlinTest.kt). You may find it easier to read and use.

### Steps Logging
We use the `step` wrapper method to make test logs easy to read. The example simple `StepLogger` shows how useful it can be. 
For instance, by implementing your own `StepProcessor`, you can extend the steps workflow and connect to the [allure report](https://docs.qameta.io/allure/) framework.

### FAQ
[FAQ](/ui-test-example/FAQ.md)