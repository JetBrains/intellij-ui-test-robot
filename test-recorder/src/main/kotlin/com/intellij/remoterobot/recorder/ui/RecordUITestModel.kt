// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.remoterobot.recorder.RobotMouseEventService
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.steps.common.CommonStepModel
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import javax.swing.DefaultListModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

internal class RecordUITestModel(val disposable: Disposable) : DefaultListModel<StepModel>() {
    private val recordMouseEventService = RobotMouseEventService {
        addElement(it)
    }

    init {
        recordMouseEventService.activate()
    }

    val useBundleKeys = ObservableField(false).apply {
        recordMouseEventService.useBundleKeys = value
        onChanged { recordMouseEventService.useBundleKeys = value }
    }

    // todo: support java
    val code: String
        get() = buildString {
            // imports
            if (elements().toList().any { it is MouseEventStepModel }) {
                append("import com.intellij.remoterobot.utils.component\n")
            }
            if (elements().toList().any { it is CommonStepModel }) {
                append("import com.intellij.remoterobot.steps.CommonSteps\n")
            }
            append("import java.util.Duration\n")

            // variables
            append("\n")
            if (elements().toList().any { it is CommonStepModel }) {
                append("val steps = CommonSteps(remoteRobot)\n")
            }

            // steps
            append("with(remoteRobot) {\n")
            elements().toList().forEach {
                append(it.generateStepCode() + "\n")
            }
            append("}")
        }

    fun onCodeUpdated(listener: (String) -> Unit) {
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent?) {
                listener(code)
            }

            override fun intervalRemoved(e: ListDataEvent?) {
                listener(code)
            }

            override fun contentsChanged(e: ListDataEvent?) {
                listener(code)
            }
        })
    }

    fun stop() = recordMouseEventService.deactivate()
}

internal class ObservableField<T>(initValue: T) {
    private val listeners: MutableList<(T) -> Unit> = mutableListOf()

    fun onChanged(action: (T) -> Unit) = listeners.add(action)

    fun removeListener(action: (T) -> Unit) = listeners.removeIf { it == action }

    var value: T = initValue
        set(value) {
            if (field == value) return
            field = value
            listeners.forEach { it.invoke(value) }
        }
}

