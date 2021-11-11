### Why on my mac the UI click are not working

On mac computers, you will need to allow the IDE to control their computer:
```Security & Privacy``` -> ```Accessibility``` -> ```Allow the apps below to control your computer``` and select the
IDE you are working with

### Can I run the tests on github actions
Yes, please check our [workflow](https://github.com/JetBrains/intellij-ui-test-robot/blob/master/.github/workflows/runTest.yml) for the UI tests example project 
### Accept End User Agreement/privacy policy and disable consents
```
runIdeForUiTests {
    systemProperty("jb.privacy.policy.text", "<!--999.999-->")
    systemProperty("jb.consents.confirmation.enabled", "false")
}
```

If the commands have now been executed and a task is now started with xvfb, it will start normally and function properly!

Unfortunately, this cannot be implemented directly in Github Actions, as the commands (access on /root/...) cannot be executed.
However, this can be implemented using a separate docker container.
For an example, see [CodeTester-IDEA](https://github.com/fxnm/CodeTester-IDEA/pull/19).

### Pass ide license
Find `idea.key` file in configuration directory of [activated ide](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs) 
and copy it to new folder and then pass this folder as configuration directory to `runIdeForUiTests` task.

```
runIdeForUiTests {
    configDirectory file('/path/to/folder')
}
```

### How can I switch off EAP Login
Since 2021.3 EAP version of paid ideas demand either login or license to run. 
For UI tests it is easier to use license:
 - put license file(idea.key, clion.key...) to the config directory `build/idea-sandbox/config-uiTest` or you can [specify it](#pass-ide-license))
 - run ide with property `eap.require.license = true`

```
runIdeForUiTests {
    systemProperty "eap.require.license", "true"
}
```

### Video recording
This project contains usage example of test recording. It uses `FedericoCarboni/setup-ffmpeg` github action and `com.automation-remarks:video-recorder-junit5` library with extension for JUnit5.
Check [video-recorder-java](https://automation-remarks.com/video-recorder-java/) for more information.