package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.recorder.steps.keyboard.TextTypingStepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import javax.swing.JComponent

internal class CreateNewTypingDialogWrapper(private val stepModel: TextTypingStepModel) : DialogWrapper(true) {

    init {
        init()
        title = RecordUITestFrame.UI_TEST_RECORDER_TITLE
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
                    .addLabeledComponent("Text to type", JBTextField(model.text).apply {
                        isVisible = true
                        requestFocus()
                        this.document.whenTextChanged {
                            model.text = text
                        }
                    })
                    .panel
            )
        }
    }
}