// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services

import com.intellij.openapi.application.WriteIntentReadAction
import com.intellij.remoterobot.data.*
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextParser
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCache
import com.intellij.remoterobot.robot.SmoothRobot
import com.intellij.remoterobot.services.js.JavaScriptExecutor
import com.intellij.remoterobot.services.xpath.XpathSearcher
import com.intellij.remoterobot.utils.LruCache
import org.assertj.swing.edt.GuiActionRunner.execute
import org.assertj.swing.edt.GuiQuery
import org.assertj.swing.exception.ComponentLookupException
import java.awt.Component
import java.awt.Container
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*
import javax.imageio.ImageIO

@Suppress("UNCHECKED_CAST")
class IdeRobot(
    private val textToKeyCache: TextToKeyCache,
    private val jsExecutor: JavaScriptExecutor,
    private val lambdaLoader: LambdaLoader,
) {

    private val componentContextCache = Collections.synchronizedMap(LruCache<String, ComponentContext>())
    private val robot = SmoothRobot()
    private val xpathSearcher = XpathSearcher(textToKeyCache)

    fun find(lambdaContainer: ObjectContainer): Result<RemoteComponent> {
        val lambda = lambdaLoader.getFunction(lambdaContainer) as RobotContext.(c: Component) -> Boolean
        return getResult(RobotContext(robot)) { ctx ->
            val c = robot.finder().find { ctx.lambda(it) }
            val id = addComponentToStorage(c)
            RemoteComponent(id, c)
        }

    }

    fun find(containerId: String, lambdaContainer: ObjectContainer): Result<RemoteComponent> {
        val lambda = lambdaLoader.getFunction(lambdaContainer) as RobotContext.(c: Component) -> Boolean
        val component = componentContextCache[containerId]?.component
            ?: throw IllegalStateException("Unknown component id $containerId")

        if (component is Container) {
            return getResult(RobotContext(robot)) { ctx ->
                val c = robot.finder().find(component) { ctx.lambda(it) }
                val id = addComponentToStorage(c)
                RemoteComponent(id, c)
            }
        } else throw IllegalStateException("Component is not a container")
    }

    fun findByXpath(xpath: String): Result<RemoteComponent> {
        return getResult(RobotContext(robot)) {
            val c = xpathSearcher.findComponent(xpath, null)
            val id = addComponentToStorage(c)
            RemoteComponent(id, c)
        }
    }

    fun findByXpath(containerId: String, xpath: String): Result<RemoteComponent> {
        val component = componentContextCache[containerId]?.component
            ?: throw IllegalStateException("Unknown component id $containerId")

        if (component is Container) {
            return getResult(RobotContext(robot)) {
                val c = xpathSearcher.findComponent(xpath, component)
                val id = addComponentToStorage(c)
                RemoteComponent(id, c)
            }
        } else throw IllegalStateException("Component is not a container")
    }

    fun findParentOf(containerId: String, lambdaContainer: ObjectContainer): Result<RemoteComponent> {
        val lambda = lambdaLoader.getFunction(lambdaContainer) as RobotContext.(c: Component) -> Boolean
        val component = componentContextCache[containerId]?.component
            ?: throw IllegalStateException("Unknown component id $containerId")

        return getResult(RobotContext(robot)) { ctx ->
            var c: Component
            c = component
            var parent: Component? = null
            while (c.parent != null) {
                c = c.parent
                if (lambda(ctx, c)) {
                    parent = c
                    break
                }
            }

            if (parent == null) {
                throw ComponentLookupException("Unable to find parent of component ($component)")
            }

            val id = addComponentToStorage(parent)
            RemoteComponent(id, parent)
        }
    }

    fun findAll(lambdaContainer: ObjectContainer): Result<List<RemoteComponent>> {
        val lambda = lambdaLoader.getFunction(lambdaContainer) as RobotContext.(c: Component) -> Boolean

        return getResult(RobotContext(robot)) { ctx ->
            robot.finder()
                .findAll { ctx.lambda(it) }
                .map { component ->
                    val id = addComponentToStorage(component)
                    RemoteComponent(id, component)
                }
        }
    }

    fun findAll(containerId: String, lambdaContainer: ObjectContainer): Result<List<RemoteComponent>> {
        val lambda = lambdaLoader.getFunction(lambdaContainer) as RobotContext.(c: Component) -> Boolean
        val component = componentContextCache[containerId]?.component
            ?: throw IllegalStateException("Unknown component id $containerId")

        if (component is Container) {
            return getResult(RobotContext(robot)) { ctx ->
                robot.finder()
                    .findAll(component) { ctx.lambda(it) }
                    .map { component ->
                        val id = addComponentToStorage(component)
                        RemoteComponent(id, component)
                    }
            }
        } else throw IllegalStateException("Component is not a container")
    }

    fun findAllByXpath(xpath: String): Result<List<RemoteComponent>> {
        return getResult(RobotContext(robot)) {
            xpathSearcher.findComponents(xpath, null)
                .map { component ->
                    val id = addComponentToStorage(component)
                    RemoteComponent(id, component)
                }
        }
    }

    fun findAllByXpath(containerId: String, xpath: String): Result<List<RemoteComponent>> {
        val component = componentContextCache[containerId]?.component
            ?: throw IllegalStateException("Unknown component id $containerId")
        return findAllByXpath(component, xpath)
    }

    fun findAllByXpath(component: Component, xpath: String): Result<List<RemoteComponent>> {
        if (component is Container) {
            return getResult(RobotContext(robot)) {
                xpathSearcher.findComponents(xpath, component)
                    .map { component ->
                        val id = addComponentToStorage(component)
                        RemoteComponent(id, component)
                    }
            }
        } else throw IllegalStateException("Component is not a container")
    }

    fun doAction(actionContainer: ObjectContainer): Result<Unit> {
        return getResult(RobotContext(robot)) { ctx ->
            if (actionContainer.runInEdt) {
                runInEdtWithWIL {
                    val action =
                        lambdaLoader.getFunction(actionContainer) as RobotContext.() -> Unit
                    ctx.action()
                }
            } else {
                val action = lambdaLoader.getFunction(actionContainer) as RobotContext.() -> Unit
                ctx.action()
            }
        }
    }

    fun doAction(componentId: String, actionContainer: ObjectContainer): Result<Unit> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) { ctx ->
            if (actionContainer.runInEdt) {
                runInEdtWithWIL {
                    val action =
                        lambdaLoader.getFunction(actionContainer) as ComponentContext.() -> Unit
                    ctx.action()
                }
            } else {
                val action = lambdaLoader.getFunction(actionContainer) as ComponentContext.() -> Unit
                ctx.action()
            }
        }
    }


    fun extractComponentData(componentId: String): Result<ComponentData> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) {
            val data = TextParser.parseComponent(it.component, textToKeyCache)
            return@getResult ComponentData(data.toList())
        }
    }

    fun retrieveText(componentId: String, actionContainer: ObjectContainer): Result<String> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) { ctx ->
            val action = lambdaLoader.getFunction(actionContainer) as ComponentContext.() -> String
            return@getResult ctx.action()
        }
    }

    fun retrieveAny(actionContainer: ObjectContainer): Result<Serializable> {
        return getResult(RobotContext(robot)) { ctx ->
            if (actionContainer.runInEdt) {
                runInEdtWithWIL {
                    val action =
                        lambdaLoader.getFunction(actionContainer) as RobotContext.() -> Serializable
                    ctx.action()
                }
            } else {
                val action = lambdaLoader.getFunction(actionContainer) as RobotContext.() -> Serializable
                return@getResult ctx.action()
            }
        }
    }

    fun retrieveAny(componentId: String, actionContainer: ObjectContainer): Result<Serializable> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) { ctx ->
            if (actionContainer.runInEdt) {
                runInEdtWithWIL {
                    val action =
                        lambdaLoader.getFunction(actionContainer) as ComponentContext.() -> Serializable
                    ctx.action()
                }
            } else {
                val action = lambdaLoader.getFunction(actionContainer) as ComponentContext.() -> Serializable
                ctx.action()
            }
        }
    }

    fun makeScreenshot(): Result<ByteArray> {
        return getResult(RobotContext(robot)) { ctx ->
            robot.makeScreenshot()
        }
    }

    fun makeScreenshot(componentId: String): Result<ByteArray> {
        val componentContext =
            componentContextCache[componentId] ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) {
            val componentLocation = componentContext.component.locationOnScreen
            robot.makeScreenshot(
                Rectangle(
                    componentLocation.x,
                    componentLocation.y,
                    componentContext.component.width,
                    componentContext.component.height
                )
            )
        }
    }

    fun makeScreenshotWithPainting(componentId: String): Result<ByteArray> {
        return execute(object : GuiQuery<Result<ByteArray>>() {
            override fun executeInEDT(): Result<ByteArray> {
                val componentContext =
                    componentContextCache[componentId] ?: throw IllegalStateException("Unknown component id $componentId")
                return getResult(componentContext) {
                    val component = componentContext.component
                    val capturedImage = BufferedImage(
                        component.bounds.width,
                        component.bounds.height,
                        BufferedImage.TYPE_INT_ARGB
                    )
                    component.paint(capturedImage.graphics)
                    return@getResult ByteArrayOutputStream().use { b ->
                        ImageIO.write(capturedImage, "png", b)
                        b.toByteArray()
                    }
                }
            }
        })
    }

    //----------------------------------
    // JavaScript

    fun doAction(script: String, runInEdt: Boolean): Result<Unit> {
        return getResult(RobotContext(robot)) { ctx ->
            if (runInEdt) {
                runInEdtWithWIL {
                    jsExecutor.execute(script, ctx)
                }
            } else {
                jsExecutor.execute(script, ctx)
            }
            Unit
        }
    }

    fun doAction(componentId: String, script: String, runInEdt: Boolean): Result<Unit> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")
        return getResult(componentContext) { ctx ->
            if (runInEdt) {
                runInEdtWithWIL {
                    jsExecutor.execute(script, ctx)
                }
            } else {
                jsExecutor.execute(script, ctx)
            }
            Unit
        }
    }

    fun retrieveAny(script: String, runInEdt: Boolean): Result<Serializable> {
        return getResult(RobotContext(robot)) { ctx ->
            if (runInEdt) {
                runInEdtWithWIL {
                    val result = jsExecutor.execute(script, ctx)
                    if (result != null && result is Serializable) {
                        return@runInEdtWithWIL result
                    }
                    throw ScriptMustReturnSerializableException(result)
                }
            } else {
                val result = jsExecutor.execute(script, ctx)
                if (result != null && result is Serializable) {
                    return@getResult result as Serializable
                }
                throw ScriptMustReturnSerializableException(result)
            }
        }
    }

    fun retrieveAny(componentId: String, script: String, runInEdt: Boolean): Result<Serializable> {
        val componentContext = componentContextCache[componentId]
            ?: throw IllegalStateException("Unknown component id $componentId")

        return getResult(componentContext) { ctx ->
            if (runInEdt) {
                runInEdtWithWIL {
                    val result = jsExecutor.execute(script, ctx)
                    if (result != null && result is Serializable) {
                        return@runInEdtWithWIL result
                    }
                    throw ScriptMustReturnSerializableException(result)
                }
            } else {
                val result = jsExecutor.execute(script, ctx)
                if (result != null && result is Serializable) {
                    return@getResult result as Serializable
                }
                throw ScriptMustReturnSerializableException(result)
            }
        }
    }

    class ScriptMustReturnSerializableException(value: Any?) :
        IllegalArgumentException("Script must return 'Serializable' but '$value' has type '${value?.javaClass}'")

    //----------------------------------

    private fun addComponentToStorage(component: Component): String {
        val uid = UUID.randomUUID().toString()
        componentContextCache[uid] = ComponentContext(robot, component)
        return uid
    }

    private fun <T, C : RemoteLoggableContext> getResult(ctx: C, function: (C) -> T?): Result<T> {
        val startTime = System.currentTimeMillis()
        return try {
            val result = function(ctx)
            val time = System.currentTimeMillis() - startTime
            Result(ctx.log.getLog(), time, result)
        } catch (e: Throwable) {
            val time = System.currentTimeMillis() - startTime
            Result(ctx.log.getLog(), time, null, e)
        }
    }

    fun highlight(x: Int, y: Int, width: Int, height: Int) {
        robot.moveMouse(x + width / 2, y + height / 2)
    }

    data class Result<T>(val logs: String, val time: Long, val data: T? = null, val exception: Throwable? = null)

    // RemoteDev


}

private fun <T> runInEdtWithWIL(block: () -> T): T {
    return execute(object : GuiQuery<T>() {
        override fun executeInEDT(): T {
            return WriteIntentReadAction.compute<T> {
                block()
            }
        }
    })
}
