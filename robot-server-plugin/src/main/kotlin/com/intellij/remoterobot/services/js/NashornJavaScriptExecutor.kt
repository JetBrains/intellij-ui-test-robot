package com.intellij.remoterobot.services.js

import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import javax.script.ScriptContext
import javax.script.ScriptException
import javax.script.SimpleScriptContext

class NashornJavaScriptExecutor: JavaScriptExecutor {
    private val engine = NashornScriptEngineFactory().getScriptEngine(javaClass.classLoader)

    override fun execute(script: String, componentContext: ComponentContext): Any? {
        return executeWithContext(script) {
            setAttribute("robot", componentContext.robot, ScriptContext.ENGINE_SCOPE)
            setAttribute("component", componentContext.component, ScriptContext.ENGINE_SCOPE)
        }
    }

    override fun execute(script: String, robotContext: RobotContext): Any? {
        return executeWithContext(script) {
            setAttribute("robot", robotContext.robot, ScriptContext.ENGINE_SCOPE)
        }
    }

    private fun executeWithContext(
        script: String,
        contextFunction: ScriptContext.() -> Unit
    ): Any? {
        val ctx: ScriptContext = SimpleScriptContext()
        ctx.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)
        ctx.contextFunction()
        try {
            return engine.eval(script, ctx)
        } catch (e: ScriptException) {
            throw IllegalArgumentException("Error running script(line: ${e.lineNumber}, column: ${e.columnNumber})\n${e.message}")
        }
    }
}