package com.intellij.remoterobot.steps

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

annotation class Step(val title: String, val stepNameTemplate: String)
annotation class StepParameter(val name: String, val defaultValue: String)

class CommonSteps(private val remoteRobot: RemoteRobot) {

    @Step("Invoke Action ID", "Invoke action {1}")
    fun invokeAction(@StepParameter("Action ID", "CloseProject") actionId: String) {
        remoteRobot.runJs(
            """
            const actionId = "$actionId";
            const actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance();
            const action = actionManager.getAction(actionId);
            actionManager.tryToExecute(action, com.intellij.openapi.ui.playback.commands.ActionCommand.getInputEvent(actionId), null, null, true);
        """, true
        )
    }

    @Step("Wait seconds", "Wait {1} seconds")
    fun wait(@StepParameter("Seconds", "10") seconds: Int) {
        Thread.sleep(seconds * 1000L)
    }

    @Step("Wait millis", "Wait {1} millis")
    fun waitMs(@StepParameter("Millis", "1000") millis: Long) {
        Thread.sleep(millis)
    }

    @Step("Wait for smart mode", "Wait for smart mode {1} minutes")
    fun waitForSmartMode(@StepParameter("Minutes", "5") minutes: Int) {
        waitFor(Duration.ofMinutes(minutes.toLong())) { isDumbMode().not() }
    }

    fun isDumbMode(): Boolean {
        return remoteRobot.callJs(
            """
            const frameHelper = com.intellij.openapi.wm.impl.ProjectFrameHelper.getFrameHelper(component)
            if (frameHelper) {
                const project = frameHelper.getProject()
                project ? com.intellij.openapi.project.DumbService.isDumb(project) : true
            } else { 
                true 
            }
        """, true
        )
    }
}