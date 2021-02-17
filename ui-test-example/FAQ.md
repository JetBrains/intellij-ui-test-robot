### Why on my mac the UI click are not working

On mac computers, you will need to allow the IDE to control their computer:
```Security & Privacy``` -> ```Accessibility``` -> ```Allow the apps below to control your computer``` and select the
IDE you are working with

### Can I run the tests on github actions
The problem is that when you try tasks like `runIde` or `runIdeForUiTests` in an environment without a graphical user interface, they don't start with the error no graphical user interface.

This problem can be solved by using (under Linux) a program like `xvfb`, which simulates a display. If you now start tasks like `runIde` or `runIdeForUiTests` with `xvfb`, they execute but the ide does not start.
Because when the Ide is starting for the first time, the terms and conditions must be accepted and the sharing of data must be allowed or prohibited. Only then does the Ide start normally.

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
