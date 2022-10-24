// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.remoterobot.recorder.RobotMouseEventService
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.isThisFromRecordTestFrame
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewMouseEventStepDialogWrapper
import javax.swing.DefaultListModel

internal class RecordUITestModel(val disposable: Disposable) : DefaultListModel<MouseEventStepModel>() {
    private val service = RobotMouseEventService { addNewStep(it) }

    init {
        service.activate()
    }

    private var selectedStep: MouseEventStepModel? = null

    fun select(step: MouseEventStepModel?) {
        selectedStep = step
    }

    val code: String
        get() = buildString {
            append("with(remoteRobot) {\n")
            elements().toList().forEach {
                append(it.generateStep() + "\n")
            }
            append("}")
        }

    fun updateCode() {
        codeUpdatedListeners.forEach { listener -> listener.invoke() }
    }

    private val codeUpdatedListeners = mutableListOf<() -> Unit>()
    fun onCodeUpdated(function: () -> Unit) {
        codeUpdatedListeners.add(function)
    }

    fun stop() = service.deactivate()

    private fun addNewStep(stepModel: MouseEventStepModel) {
        if (isThisFromRecordTestFrame(stepModel.component)) return
        if (CreateNewMouseEventStepDialogWrapper(stepModel).showAndGet()) {
            addElement(stepModel)
            updateCode()
        }
    }
}

internal class ObservableField<T>(initValue: T) {
    private val listeners: MutableList<(T) -> Unit> = mutableListOf()

    fun onChanged(action: (T) -> Unit) = listeners.add(action)
    var value: T = initValue
        set(value) {
            field = value
            listeners.forEach { it.invoke(value) }
        }
}

