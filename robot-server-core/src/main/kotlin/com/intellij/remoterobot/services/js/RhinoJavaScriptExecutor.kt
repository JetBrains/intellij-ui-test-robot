// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.js

import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext
import org.mozilla.javascript.*
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader

class RhinoJavaScriptExecutor(private val appClassLoader: ClassLoader? = null) : JavaScriptExecutor {
    private val globalObjectMap by lazy { mutableMapOf<String, Any>() }
    override fun execute(script: String, componentContext: ComponentContext): Any? {
        return executeWithContext(script) {
            defineProperty("robot", componentContext.robot, 0)
            defineProperty("log", componentContext.log, 0)
            defineProperty("component", componentContext.component, 0)
            defineProperty("ctx", componentContext.objects, 0)
            defineProperty("local", componentContext.objects, 0)
            defineProperty("global", globalObjectMap, 0)
        }
    }

    override fun execute(script: String, robotContext: RobotContext): Any? {
        return executeWithContext(script) {
            defineProperty("robot", robotContext.robot, 0)
            defineProperty("log", robotContext.log, 0)
            defineProperty("global", globalObjectMap, 0)
        }
    }

    private fun executeWithContext(
        script: String,
        contextFunction: ScriptableObject.() -> Unit
    ): Any? {
        val context = Context.enter().apply {
            val performancePluginId = PluginId.getId("com.jetbrains.performancePlugin")
            val performancePlugin = PluginManagerCore.getPlugin(performancePluginId)

            val defaultClassLoader = appClassLoader ?: javaClass.classLoader
            val perfPluginClassLoader =
                if (PluginManager.isPluginInstalled(performancePluginId) && performancePlugin != null) {
                    performancePlugin.pluginClassLoader
                } else {
                    null
                }

            applicationClassLoader = if (perfPluginClassLoader != null) MultipleParentClassLoader(
                listOf(
                    defaultClassLoader,
                    perfPluginClassLoader
                )
            )
            else MultipleParentClassLoader(listOf(defaultClassLoader))
            optimizationLevel = 9
            languageVersion = Context.VERSION_ES6
        }

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