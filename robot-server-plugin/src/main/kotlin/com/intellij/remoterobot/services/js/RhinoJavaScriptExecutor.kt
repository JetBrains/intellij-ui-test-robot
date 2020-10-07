package com.intellij.remoterobot.services.js

import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext
import org.mozilla.javascript.*

class RhinoJavaScriptExecutor : JavaScriptExecutor {
    override fun execute(script: String, componentContext: ComponentContext): Any? {
        return executeWithContext(script) {
            defineProperty("robot", componentContext.robot, 0)
            defineProperty("log", componentContext.log, 0)
            defineProperty("component", componentContext.component, 0)
            defineProperty("ctx", componentContext.objects, 0)
        }
    }

    override fun execute(script: String, robotContext: RobotContext): Any? {
        return executeWithContext(script) {
            defineProperty("robot", robotContext.robot, 0)
            defineProperty("log", robotContext.log, 0)
        }
    }

    private fun executeWithContext(
        script: String,
        contextFunction: ScriptableObject.() -> Unit
    ): Any? {
        val context = Context.enter()

        context.applicationClassLoader = javaClass.classLoader
        context.optimizationLevel = 9
        context.languageVersion = Context.VERSION_ES6
        try {
            val scope = ImporterTopLevel(context)
            scope.contextFunction()
            val jsScript = context.compileString(addImport(script), "js", 1, null)
            return when (val result = jsScript.exec(context, scope)) {
                is NativeJavaObject -> result.unwrap()
                is Undefined -> null
                else -> result
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        } finally {
            Context.exit()
        }
    }

    private fun addImport(script: String): String {
        return """
            importPackage(java.awt);
            importPackage(java.util);
            importPackage(java.lang);
            importPackage(org.assertj.swing.core);
            importPackage(org.assertj.swing.fixture);

            $script
        """
    }
}