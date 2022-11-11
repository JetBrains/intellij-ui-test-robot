package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.recorder.steps.keyboard.TextTypingStepModel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.JComponent
import javax.swing.JTextArea

internal class CreateNewTypingDialogWrapper(private val stepModel: TextTypingStepModel) : DialogWrapper(true) {

    init {
        init()
        title = "Create text typing step"
    }

    override fun createCenterPanel(): JComponent {
        val actionPanel = ActionPanel(stepModel)
        return BorderLayoutPanel().apply {
            addToTop(actionPanel.apply {
                showTextArea()
            })
            size.width = 300
        }
    }

    internal class ActionPanel(private val model: TextTypingStepModel) : BorderLayoutPanel() {

        fun showTextArea() {
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Text to type", JTextArea(5, 50).apply {
                        isVisible = true
                        requestFocus()
                        this.document.whenTextChanged {
                            model.text = if (this.text.contains("\n")) asMultiline(this.text) else this.text
                        }
                    })
                    .panel
            )
        }

        private fun asMultiline(text: String) =
            "\"\"\n${text.lines().joinToString("") { "             $it\n" }}          \"\""
    }
}