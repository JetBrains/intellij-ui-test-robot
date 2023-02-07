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
import com.intellij.remoterobot.recorder.steps.keyboard.TextHotKeyStepModel
import com.intellij.remoterobot.recorder.steps.keyboard.TextTypingStepModel
import com.intellij.remoterobot.recorder.steps.mouse.MouseClickOperation
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame
import com.intellij.remoterobot.recorder.ui.RecordUITestModel
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewMouseEventStepDialogWrapper
import org.assertj.swing.core.MouseButton
import java.awt.AWTEvent
import java.awt.Component
import java.awt.Container
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_SHIFT
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.MOUSE_PRESSED
import javax.swing.RootPaneContainer
import javax.swing.SwingUtilities


internal class RobotEventService(private val recordTestModel: RecordUITestModel) {
    private val locatorGenerator = LocatorGenerator()
    private var disposable: Disposable? = null

    private var isActive: Boolean = false
    var useBundleKeys: Boolean = true
    var isRecordAllMode: Boolean = false

    fun activate() {
        disposable = Disposer.newDisposable()
        val globalActionListener = object : AnActionListener {
            override fun beforeActionPerformed(action: AnAction, dataContext: DataContext, event: AnActionEvent) {

            }
        }

        val globalAwtProcessor = IdeEventQueue.EventDispatcher { awtEvent ->
            try {
                findComponent(awtEvent)?.takeIf {
                    RecordUITestFrame.isThisFromRecordTestFrame(it).not()
                }?.let {
                    when (awtEvent) {
                        is MouseEvent -> processMouseEvent(awtEvent, it)
                        is KeyEvent -> processKeyEvent(awtEvent, it)
                    }
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


    private fun processMouseEvent(event: MouseEvent, component: Component) {
        if (event.isShiftDown || event.isControlDown || event.isMetaDown || isRecordAllMode) {
            when (event.id) {
                MOUSE_PRESSED -> {
                    processClick(event, component)
                }
                //MOUSE_DRAGGED -> { processDragging(event) }
                //MOUSE_RELEASED -> { stopDragging(event) }
            }
        }
    }

    private fun processClick(event: MouseEvent, component: Component) {
        val convertedPoint = Point(
            event.locationOnScreen.x - component.locationOnScreen.x,
            event.locationOnScreen.y - component.locationOnScreen.y
        )

        val xpath = locatorGenerator.generateXpath(component, useBundleKeys)
        val texts = TextParser.parseComponent(component, TextToKeyCache)

        val mouseEventOperation = if (isRecordAllMode) {
            val mouseButton = when (event.button) {
                1 -> MouseButton.LEFT_BUTTON
                3 -> MouseButton.RIGHT_BUTTON
                else -> MouseButton.LEFT_BUTTON
            }
            MouseClickOperation(mouseButton, where = convertedPoint)
        } else {
            MouseClickOperation()
        }
        val newStepModel =
            MouseEventStepModel(
                convertedPoint,
                xpath,
                texts,
                component.generateName(texts),
                useBundleKeys,
                mouseEventOperation
            )
        addNewMouseEvenStep(newStepModel)

    }

    private fun findComponent(event: AWTEvent): Component? {
        when (event) {
            is MouseEvent -> {
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
                if (actualComponent == null) actualComponent =
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
                return actualComponent
            }

            is KeyEvent -> {
                return KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
            }

            else -> return null
        }
    }

    private fun addNewMouseEvenStep(stepModel: MouseEventStepModel) {
        if (isRecordAllMode) {
            currentEnterTextStep = null
            recordTestModel.addElement(stepModel)
            return
        }
        if (CreateNewMouseEventStepDialogWrapper(stepModel).showAndGet()) {
            recordTestModel.addElement(stepModel)
        }
    }

    private var currentEnterTextStep: TextTypingStepModel? = null
    private fun processKeyEvent(keyEvent: KeyEvent, component: Component) {
        println(keyEvent)
        when (keyEvent.id) {
            KeyEvent.KEY_PRESSED -> {
                if (isRecordAllMode && keyEvent.isTextTyping()) {
                    if (currentEnterTextStep == null) {
                        currentEnterTextStep = TextTypingStepModel("Type text", "")
                        recordTestModel.addElement(currentEnterTextStep!!)
                    }
                    currentEnterTextStep?.addChar(keyEvent.keyChar)
                    recordTestModel.forceUpdateCode()
                } else if (keyEvent.keyCode != VK_SHIFT) {
                    currentEnterTextStep = null

                    val singleKeyStepModel = TextHotKeyStepModel("Press Key", "", "")
                    singleKeyStepModel.processKeyEvent(keyEvent)

                    if (singleKeyStepModel.shortcutCode.isNotEmpty()) {
                        recordTestModel.addElement(singleKeyStepModel)
                    }
                }
            }
        }
    }

    private fun KeyEvent.isTextTyping(): Boolean {
        if (Character.isLetterOrDigit(keyChar)) return true
        if (keyChar.toString() in " ,!@#$%^&*()-+_\\[]{};'\"<>/:") return true
        return false
    }
}