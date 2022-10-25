package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel

internal class TextHotKeyStepModel(
    override var name: String,
    var text: String
) : StepModel {

    override fun generateStep(): String {
        return """
      |     step("$name") {
      |        keyboard {
      |          hotkey("$text")
      |        }
      |     }
    """.trimMargin()
    }
}