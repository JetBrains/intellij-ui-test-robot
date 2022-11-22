package com.intellij.remoterobot.recorder.steps.common

import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.ui.ObservableField
import com.intellij.remoterobot.steps.CommonSteps
import com.intellij.remoterobot.steps.Step
import com.intellij.remoterobot.steps.StepParameter
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.properties.Delegates

internal class CommonStepMeta(
    private val method: Method
) {
    val title: String
        get() = method.annotations.filterIsInstance<Step>().firstOrNull()?.title ?: "---"

    val methodName: String
        get() = method.name

    val parameters: List<StepParameterMeta> = method.parameters.map { StepParameterMeta(it) }

    val stepNameTemplate: String
        get() = method.annotations.filterIsInstance<Step>().firstOrNull()?.stepNameTemplate ?: "---"
}

internal class StepParameterMeta(private val parameter: Parameter) {
    private val annotation: StepParameter? =
        parameter.annotations.filterIsInstance<StepParameter>().firstOrNull()

    val name: String = annotation?.name ?: parameter.name

    var value: String = annotation?.defaultValue ?: ""

    val type: Class<*> = parameter.type

    val uiType: StepParameter.UiType = annotation?.componentUiType ?: StepParameter.UiType.DEFAULT

    val typedValue: String
        get() = when (type) {
            Int::class.java, Double::class.java, Float::class.java, Long::class.java -> value
            else -> "\"$value\""
        }
}

internal class CommonStepModel(stepName: String = "", initialStepMeta: CommonStepMeta? = null) : StepModel {
    companion object {
        fun getSteps() = CommonSteps::class.java.methods.filter {
            it.annotations.filterIsInstance<Step>().isNotEmpty()
        }.map { CommonStepMeta(it) }
    }

    val observableStepName = ObservableField(stepName)
    var step: CommonStepMeta by Delegates.observable(initialStepMeta ?: getSteps().first()) { _, _, _ ->
        updateName()
    }

    override fun generateStepCode(): String {
        return """
      |     step("$name") {
      |        steps.${step.methodName}(${step.parameters.joinToString(", ") { it.typedValue }})
      |     }
    """.trimMargin()
    }

    fun updateName() {
        var name = step.stepNameTemplate
        step.parameters.forEachIndexed { n, parameter -> name = name.replace("{${n + 1}}", parameter.value) }
        observableStepName.value = name
    }

    override val name: String
        get() = observableStepName.value

    fun copy() = CommonStepModel(observableStepName.value, step)
}