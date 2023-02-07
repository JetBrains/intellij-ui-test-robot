// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.remoterobot.recorder.RobotEventService
import com.intellij.remoterobot.recorder.steps.GroupableStep
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.steps.common.CommonStepModel
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import javax.swing.DefaultListModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

internal class RecordUITestModel(val disposable: Disposable) : DefaultListModel<StepModel>() {
    private val recordMouseEventService = RobotEventService(this)

    init {
        recordMouseEventService.activate()
    }

    val useBundleKeys = ObservableField(false).apply {
        recordMouseEventService.useBundleKeys = value
        onChanged { recordMouseEventService.useBundleKeys = value }
    }

    val recordAllMode = ObservableField(false).apply {
        recordMouseEventService.isRecordAllMode = value
        onChanged { recordMouseEventService.isRecordAllMode = value }
    }

    // todo: support java
    val code: String
        get() = buildString {
            // imports
            if (elements().toList().any { it is MouseEventStepModel }) {
                append("import com.intellij.remoterobot.utils.component\n")
                append("import java.awt.Point\n")
            }
            if (elements().toList().any { it is CommonStepModel }) {
                append("import com.intellij.remoterobot.steps.CommonSteps\n")
            }
            append("import java.util.Duration\n")
            append("\n")
            append("//======================\n")

            // variables
            append("\n")
            if (elements().toList().any { it is CommonStepModel }) {
                append("val steps = CommonSteps(remoteRobot)\n")
            }

            // steps
            append("with(remoteRobot) {\n")
            val steps = elements().toList()
            steps.forEachIndexed { i, step ->
                when (step) {
                    is GroupableStep -> {
                        val prevStep = steps.getOrNull(i - 1)
                        if (step.isTheSameGroup(prevStep).not()) {
                            append(step.prefixCode().makeIndents(1) + "\n")
                        }
                        append(step.generateStepCode().makeIndents(2) + "\n")
                        val nextStep = steps.getOrNull(i + 1)
                        if (step.isTheSameGroup(nextStep).not()) {
                            append(step.postfixCode().makeIndents(1) + "\n")
                        }
                    }

                    else -> append(step.generateStepCode().makeIndents(1) + "\n")
                }
            }
            append("}")
        }

    private fun String.makeIndents(count: Int) = split("\n").joinToString("\n") {
        buildString {
            repeat(count) {
                append("\t")
            }
            append(it)
        }
    }

    private val forceUpdateCodeListeners: MutableList<(String) -> Unit> = mutableListOf()
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
        forceUpdateCodeListeners.add(listener)
    }


    fun forceUpdateCode() {
        forceUpdateCodeListeners.forEach {
            it.invoke(code)
        }
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

