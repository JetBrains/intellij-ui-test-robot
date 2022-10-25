// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.remoterobot.recorder.RobotMouseEventService
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.steps.common.CommonStepModel
import javax.swing.DefaultListModel

internal class RecordUITestModel(val disposable: Disposable) : DefaultListModel<StepModel>() {
    private val recordMouseEventService = RobotMouseEventService {
        addElement(it)
        updateCode()
    }

    init {
        recordMouseEventService.activate()
    }

    private var selectedStep: StepModel? = null

    fun select(step: StepModel?) {
        selectedStep = step
    }

    // todo: support java
    val code: String
        get() = buildString {
            if (elements().toList().any { it is CommonStepModel }) {
                append("val steps = CommonSteps(remoteRobot)\n")
            }
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

    fun stop() = recordMouseEventService.deactivate()
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

