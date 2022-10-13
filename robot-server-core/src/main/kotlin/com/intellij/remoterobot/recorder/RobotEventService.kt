// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder

import com.intellij.application.subscribe
import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.util.Disposer
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextParser
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCache
import com.intellij.remoterobot.recorder.ui.StepModel
import java.awt.Component
import java.awt.Container
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.MOUSE_PRESSED
import javax.swing.AbstractButton
import javax.swing.RootPaneContainer
import javax.swing.SwingUtilities
import javax.swing.text.JTextComponent

internal class RobotEventService(private val newStepHandler: (StepModel) -> Unit) {
    private val locatorGenerator = LocatorGenerator()
    private var disposable: Disposable? = null

    private var isActive: Boolean = false

    // https://gitcode.net/mirrors/JetBrains/intellij-community/-/tree/193/platform/testGuiFramework/src/com/intellij/testGuiFramework/recorder
    fun activate() {
        disposable = Disposer.newDisposable()

        //val eventLogListener = object : StatisticsEventLogListener {
        //  override fun onLogEvent(validatedEvent: LogEvent, rawEventId: String?, rawData: Map<String, Any>?) {
        //    //println("$rawEventId $rawData")
        //  }
        //
        //}
        //
        //service<EventLogListenersManager>().subscribe(eventLogListener, "FUS")
        //

        val globalActionListener = object : AnActionListener {
            override fun beforeActionPerformed(action: AnAction, dataContext: DataContext, event: AnActionEvent) {
                println("IDEA is going to perform action ${action.templatePresentation.text}")
            }
        }

        val globalAwtProcessor = IdeEventQueue.EventDispatcher { awtEvent ->
            try {
                when (awtEvent) {
                    is MouseEvent -> processMouseEvent(awtEvent)
                    is KeyEvent -> processKeyEvent(awtEvent)
                }
            } catch (e: Exception) {
                println(e)
            }
            false
        }

        AnActionListener.TOPIC.subscribe(disposable, globalActionListener)
        IdeEventQueue.getInstance().addDispatcher(globalAwtProcessor, disposable)
        isActive = true
    }

    private fun processKeyEvent(awtEvent: KeyEvent) {
        println(awtEvent)
    }

    fun deactivate() {
        if (isActive) {
            println("Global action recorder is non active")
            disposable?.let {
                this.disposable = null
                Disposer.dispose(it)
            }
        }
        isActive = false
    }


    private fun processMouseEvent(event: MouseEvent) {
        if (event.isShiftDown || event.isControlDown || event.isMetaDown) {
            when (event.id) {
                MOUSE_PRESSED -> {
                    processClick(event)
                }
                //MOUSE_DRAGGED -> { processDragging(event) }
                //MOUSE_RELEASED -> { stopDragging(event) }
            }
        }
    }

    private fun processClick(event: MouseEvent) {
        val actualComponent: Component? = findComponent(event)
        if (actualComponent != null) {
            val convertedPoint = Point(
                event.locationOnScreen.x - actualComponent.locationOnScreen.x,
                event.locationOnScreen.y - actualComponent.locationOnScreen.y
            )
            val xpath = locatorGenerator.generateXpath(actualComponent)
            val texts = TextParser.parseComponent(actualComponent, true, TextToKeyCache)
            val name = "Click at ${generateComponentName(actualComponent, texts.map { it.text })}"
            val newStepModel = StepModel(name, actualComponent, convertedPoint, null, xpath, texts)
            newStepHandler(newStepModel)
        }
    }

    private fun generateComponentName(component: Component, texts: List<String>): String {
        if (texts.size in 1..3) {
            return texts.joinToString(" ") { it }
        }
        val name: String? = when (component) {
            is AbstractButton -> component.text
            is JTextComponent -> component.text?.let {
                if (it.length > 20) {
                    it.substring(0, 20)
                } else {
                    it
                }
            }

            else -> component.name
        }
        return name?.takeIf { it.isNotEmpty() } ?: component::class.java.name.substringAfterLast(".")
    }

    private fun findComponent(event: MouseEvent): Component? {
        val mousePoint = event.point
        val eventComponent = event.component
        var actualComponent: Component? = null
        when (eventComponent) {
            is RootPaneContainer -> {
                val layeredPane = eventComponent.layeredPane
                val point = SwingUtilities.convertPoint(eventComponent, mousePoint, layeredPane)
                actualComponent = layeredPane.findComponentAt(point)
            }

            is Container -> actualComponent = eventComponent.findComponentAt(mousePoint)
        }
        if (actualComponent == null) actualComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
        return actualComponent
    }
}