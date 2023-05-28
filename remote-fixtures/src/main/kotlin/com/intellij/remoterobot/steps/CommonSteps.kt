package com.intellij.remoterobot.steps

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.utils.component
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

annotation class Step(val title: String, val stepNameTemplate: String)
annotation class StepParameter(val name: String, val defaultValue: String, val componentUiType: UiType = UiType.DEFAULT) {
    enum class UiType { DEFAULT, ACTION_ID }
}

class CommonSteps(private val remoteRobot: RemoteRobot) {

    @Step("Invoke Action ID", "Invoke actionId '{1}'")
    fun invokeAction(@StepParameter("Action", "Close Project", StepParameter.UiType.ACTION_ID) actionId: String) {
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

    @Step("Open project", "Open project '{1}'")
    fun openProject(@StepParameter("Project absolute path", "") absolutePath: String) {
        remoteRobot.runJs(
            """
            importClass(com.intellij.openapi.application.ApplicationManager)
            importClass(com.intellij.ide.impl.OpenProjectTask)
           
            const projectManager = com.intellij.openapi.project.ex.ProjectManagerEx.getInstanceEx()
            let task 
            try { 
                task = OpenProjectTask.build()
            } catch(e) {
                task = OpenProjectTask.newProject()
            }
            const path = new java.io.File("$absolutePath").toPath()
           
            const openProjectFunction = new Runnable({
                run: function() {
                    projectManager.openProject(path, task)
                }
            })
           
            ApplicationManager.getApplication().invokeLater(openProjectFunction)
        """
        )
    }

    fun isDumbMode(): Boolean {
        return remoteRobot.component("//div[@class='IdeFrameImpl']").callJs(
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

    @Step("Execute cmd", "Execute cmd '{1}'")
    fun executeCmd(@StepParameter("cmd", "ls -la")cmd: String): String = remoteRobot.callJs("""
            importClass(java.lang.StringBuilder)
            let result = null;
            const builder = new StringBuilder();
            const pBuilder = new ProcessBuilder(${cmd.split(" ").joinToString(separator = "\", \"", prefix = "\"", postfix = "\"")})
                .redirectErrorStream(true);
            let p;
            try {
                let s;
                p = pBuilder.start();
                const br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null) {
                    builder.append(s + "\n")
                }
                p.waitFor();
                result =  builder.toString();
            } catch (e) {
                result = e.getMessage().toString()
            } finally {
                if (p) { p.destroy(); }
            }
            result;
        """)
}