package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.StepModel
import com.intellij.ui.KeyStrokeAdapter
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

internal class TextHotKeyStepModel(
    override var name: String,
    var shortcutCode: String,
    var shortcutText: String? = null
) : KeyboardGroupableStep(), StepModel {

    override fun generateStepCode(): String {
        return """step("$name") { hotKey($shortcutCode) }"""
    }

    fun copy(): TextHotKeyStepModel = TextHotKeyStepModel(name, shortcutCode, shortcutText)

    fun processKeyEvent(event: KeyEvent) {
        if (event.id == KeyEvent.KEY_PRESSED) {
            val keyCode = event.keyCode
            if (keyCode != KeyEvent.VK_SHIFT &&
                keyCode != KeyEvent.VK_ALT &&
                keyCode != KeyEvent.VK_CONTROL &&
                keyCode != KeyEvent.VK_ALT_GRAPH &&
                keyCode != KeyEvent.VK_META
            ) {
                val result = if (event.modifiersEx != 0)
                    "${KeyEvent.getModifiersExText(event.modifiersEx)}+${KeyEvent.getKeyText(event.keyCode)}"
                else KeyEvent.getKeyText(event.keyCode)
                shortcutText = result
                name = "Press '$result'"
                shortcutCode = if (event.modifiersEx != 0)
                    "${getModifierCodes(KeyStroke.getKeyStroke(event.keyCode, event.modifiersEx))}, ${event.keyCode}"
                else "${event.keyCode}"
            }
        }
    }
    private fun getModifierCodes(keyStroke: KeyStroke): String {
        return keyStroke.toString().substringBefore("pressed").trim().split(" ")
            .map { KeyStrokeAdapter.getKeyStroke(it).keyCode }.joinToString()
    }
}