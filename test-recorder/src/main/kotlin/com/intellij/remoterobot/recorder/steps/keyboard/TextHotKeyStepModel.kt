package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel

internal data class TextHotKeyStepModel(
    override var name: String,
    var text: String,
    var shortcut: String? = null
) : StepModel {

    override fun generateStepCode(): String {
        return """
      |     step("$name") {
      |        keyboard {
      |          hotKey($text)
      |        }
      |     }
    """.trimMargin()
    }
}