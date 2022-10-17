package com.intellij.remoterobot.recorder.steps

import com.intellij.remoterobot.data.TextData
import java.awt.Component
import java.awt.Point

class StepModel(
    var name: String,
    val component: Component,
    val point: Point,
    var action: StepAction?,
    var xpath: String,
    val texts: List<TextData>
) {

    fun generateStep(): String {
        return """
      |     step("$name") {
      |        component("$xpath")
      |          .${action?.getActionCode() ?: throw IllegalStateException("Action was not chosen")}
      |     }
    """.trimMargin()
    }
}