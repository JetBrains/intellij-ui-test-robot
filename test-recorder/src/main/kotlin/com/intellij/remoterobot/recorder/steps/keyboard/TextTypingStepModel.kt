package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel

internal class TextTypingStepModel(
    override val name: String = "Type text",
    var text: String
) : StepModel {

    override fun generateStep(): String {
        return """
      |     step("$name") {
      |        keyboard {
      |          enterText("$text")
      |        }
      |     }
    """.trimMargin()
    }
}