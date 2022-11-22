package com.intellij.remoterobot.recorder.steps.mouse

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.ui.ObservableField
import java.awt.Point
import java.time.Duration
import kotlin.properties.Delegates


internal class MouseEventStepModel(
    val point: Point,
    var xpath: String,
    val texts: List<TextData>,
    private val componentName: String,
    private val useBundleKeys: Boolean,
    operation: MouseEventOperation = MouseClickOperation(),
    stepName: String? = null,
    var searchTimeout: Duration? = null
) : StepModel {
    var operation by Delegates.observable(operation) { _, o, _ ->
        updateName()
    }

    val observableStepName = ObservableField(stepName ?: generateStepName())

    override val name: String
        get() = observableStepName.value

    override fun generateStepCode(): String {
        return """
      |     step("$name") {
      |        component("$xpath"${searchTimeout?.let { ", Duration.ofSeconds(${it.seconds})" } ?: ""})
      |          .${operation.generateActionCode(useBundleKeys)}
      |     }
    """.trimMargin()
    }

    fun copy() = MouseEventStepModel(point, xpath, texts, componentName, useBundleKeys, operation, observableStepName.value, searchTimeout)

    private fun updateName() {
        observableStepName.value = generateStepName()
    }

    private fun generateStepName(): String {
        return "${operation.name} on $componentName"
    }
}