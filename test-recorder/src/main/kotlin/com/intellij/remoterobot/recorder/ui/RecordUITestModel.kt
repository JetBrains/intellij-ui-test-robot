// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.remoterobot.recorder.RobotEventService
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.isThisFromRecordTestFrame
import javax.swing.DefaultListModel

class RecordUITestModel(val disposable: Disposable) : DefaultListModel<StepModel>() {
    private val service = RobotEventService { addNewStep(it) }

    init {
        service.activate()
    }

    private var selectedStep: StepModel? = null

    fun select(step: StepModel?) {
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

    fun codeIsUpdated() {
        codeUpdatedListeners.forEach { listener -> listener.invoke() }
    }

    private val codeUpdatedListeners = mutableListOf<() -> Unit>()
    fun onCodeUpdated(function: () -> Unit) {
        codeUpdatedListeners.add(function)
    }

    fun stop() = service.deactivate()

    private fun addNewStep(stepModel: StepModel) {
        if (isThisFromRecordTestFrame(stepModel.component)) return
        if (CreateNewStepDialogWrapper(stepModel).showAndGet()) {
            addElement(stepModel)
            println(stepModel.generateStep())

            codeIsUpdated()
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