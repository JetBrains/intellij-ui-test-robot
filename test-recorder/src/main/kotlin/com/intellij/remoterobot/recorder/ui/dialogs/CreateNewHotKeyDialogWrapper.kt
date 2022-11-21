package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.recorder.steps.keyboard.TextHotKeyStepModel
import com.intellij.ui.KeyStrokeAdapter
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.AWTEvent
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.KeyStroke

internal class CreateNewHotKeyDialogWrapper(private val stepModel: TextHotKeyStepModel) : DialogWrapper(true) {

    init {
        init()
        title = "Create new hotkey step"
    }

    override fun createCenterPanel(): JComponent {
        val actionPanel = ActionPanel(stepModel)
        return BorderLayoutPanel().apply {
            addToTop(actionPanel.apply { showHotkeySetting() })
        }
    }

    internal class ActionPanel(private val model: TextHotKeyStepModel) : BorderLayoutPanel() {

        fun showHotkeySetting() {
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addComponent(JLabel("Please, press hotkey"))
                    .addLabeledComponent("Hotkey:", HotKeyField(model))
                    .panel
            )
        }
    }

    private class HotKeyField(private val model: TextHotKeyStepModel) : JTextField() {

        init {
            enableEvents(AWTEvent.KEY_EVENT_MASK)
            focusTraversalKeysEnabled = false
            isVisible = true
        }

        override fun processKeyEvent(e: KeyEvent) {
            if (e.id == KeyEvent.KEY_PRESSED) {
                val keyCode = e.keyCode
                if (keyCode != KeyEvent.VK_SHIFT &&
                    keyCode != KeyEvent.VK_ALT &&
                    keyCode != KeyEvent.VK_CONTROL &&
                    keyCode != KeyEvent.VK_ALT_GRAPH &&
                    keyCode != KeyEvent.VK_META
                ) {
                    val result = if (e.modifiersEx != 0)
                        "${KeyEvent.getModifiersExText(e.modifiersEx)}+${KeyEvent.getKeyText(e.keyCode)}"
                    else KeyEvent.getKeyText(e.keyCode)
                    this.text = result
                    model.name = "Press hotkey '$result'"
                    model.text = if (e.modifiersEx != 0)
                        "${getModifierCodes(KeyStroke.getKeyStroke(e.keyCode, e.modifiersEx))}, ${e.keyCode}"
                    else "${e.keyCode}"
                }
            }
        }

        private fun getModifierCodes(keyStroke: KeyStroke): String {
            return keyStroke.toString().substringBefore("pressed").trim().split(" ")
                .map { KeyStrokeAdapter.getKeyStroke(it).keyCode }.joinToString()
        }
    }
}