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
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewMouseEventStepDialogWrapper
import java.awt.Component
import java.awt.Container
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.MOUSE_PRESSED
import javax.swing.RootPaneContainer
import javax.swing.SwingUtilities

internal class RobotMouseEventService(private val addMouseStepHandler: (MouseEventStepModel) -> Unit) {
    private val locatorGenerator = LocatorGenerator()
    private var disposable: Disposable? = null

    private var isActive: Boolean = false
    var useBundleKeys: Boolean = true

    fun activate() {
        disposable = Disposer.newDisposable()
        val globalActionListener = object : AnActionListener {
            override fun beforeActionPerformed(action: AnAction, dataContext: DataContext, event: AnActionEvent) {

            }
        }

        val globalAwtProcessor = IdeEventQueue.EventDispatcher { awtEvent ->
            try {
                when (awtEvent) {
                    is MouseEvent -> processMouseEvent(awtEvent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            false
        }

        AnActionListener.TOPIC.subscribe(disposable, globalActionListener)
        IdeEventQueue.getInstance().addDispatcher(globalAwtProcessor, disposable)
        isActive = true
    }

    fun deactivate() {
        if (isActive) {
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
            val xpath = locatorGenerator.generateXpath(actualComponent, useBundleKeys)
            val texts = TextParser.parseComponent(actualComponent, true, TextToKeyCache)
            val newStepModel = MouseEventStepModel(actualComponent, convertedPoint, xpath, texts, useBundleKeys)
            addNewMouseEvenStep(newStepModel)
        }
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

    private fun addNewMouseEvenStep(stepModel: MouseEventStepModel) {
        if (RecordUITestFrame.isThisFromRecordTestFrame(stepModel.component)) return
        if (CreateNewMouseEventStepDialogWrapper(stepModel).showAndGet()) {
            addMouseStepHandler(stepModel)
        }
    }
}