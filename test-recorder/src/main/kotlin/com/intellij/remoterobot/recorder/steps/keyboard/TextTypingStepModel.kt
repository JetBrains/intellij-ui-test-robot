package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel

internal data class TextTypingStepModel(
    override val name: String = "Type text",
    var text: String
) : StepModel {

    override fun generateStepCode(): String {
        return """
      |     step("$name") {
      |        keyboard {
      |          enterText("$text")
      |        }
      |     }
    """.trimMargin()
    }
}