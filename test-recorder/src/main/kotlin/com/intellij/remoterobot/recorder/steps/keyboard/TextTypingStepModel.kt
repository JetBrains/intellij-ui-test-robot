package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel

internal class TextTypingStepModel(
    override var name: String = "Type text",
    initialText: String = ""
) : KeyboardGroupableStep(), StepModel {

    private val textBuilder: StringBuilder = StringBuilder(initialText)
    var text: String
        get() = textBuilder.toString()
        set(value) {
            textBuilder.apply { clear(); append(value) }
            updateName()
        }

    override fun generateStepCode(): String {
        return """enterText("$text")"""
    }

    fun copy(): TextTypingStepModel = TextTypingStepModel(name, text)
    private fun updateName() {
        name = "Type '$text'"
    }

    fun addChar(char: Char) {
        textBuilder.append(char)
        updateName()
    }
}