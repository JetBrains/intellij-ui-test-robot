package com.intellij.remoterobot.recorder.steps.mouse

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.remoterobot.recorder.ui.ObservableField
import java.awt.Component
import java.awt.Point
import javax.swing.AbstractButton
import javax.swing.text.JTextComponent


internal class MouseEventStepModel(
    val component: Component,
    val point: Point,
    var xpath: String,
    val texts: List<TextData>
) : StepModel {
    val operation: ObservableField<MouseEventOperation> =
        ObservableField<MouseEventOperation>(MouseClickOperation(this)).apply {
            onChanged { updateName() }
        }
    override val name: String
        get() = observableStepName.value

    val observableStepName = ObservableField("")

    override fun generateStep(): String {
        return """
      |     step("$name") {
      |        component("$xpath")
      |          .${operation.value.getActionCode()}
      |     }
    """.trimMargin()
    }

    fun updateName() {
        observableStepName.value = generateStepName()
    }

    private fun generateStepName(): String {
        return "${operation.value.name} on ${generateComponentName()}"
    }

    private fun generateComponentName(): String {
        if (texts.size in 1..3) {
            return texts.joinToString(" ") { it.text }
        }
        val name: String? = when (component) {
            is AbstractButton -> component.text + " Button"
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
            .substringBefore("$")
    }
}