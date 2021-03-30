### Why on my mac the UI click are not working

On mac computers, you will need to allow the IDE to control their computer:
```Security & Privacy``` -> ```Accessibility``` -> ```Allow the apps below to control your computer``` and select the
IDE you are working with

### Can I run the tests on github actions
Yes, please check our [workflow](https://github.com/JetBrains/intellij-ui-test-robot/blob/master/.github/workflows/runTest.yml) for the UI tests example project 
#### Accept End User Agreement/privacy policy and disable consents
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
