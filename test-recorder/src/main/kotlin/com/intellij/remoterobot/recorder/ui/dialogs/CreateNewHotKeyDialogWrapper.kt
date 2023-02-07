package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.UI_TEST_RECORDER_TITLE
import com.intellij.remoterobot.recorder.steps.keyboard.TextHotKeyStepModel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.AWTEvent
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField

internal class CreateNewHotKeyDialogWrapper(private val stepModel: TextHotKeyStepModel) : DialogWrapper(true) {

    init {
        init()
        title = UI_TEST_RECORDER_TITLE
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
            text = model.shortcutText
            enableEvents(AWTEvent.KEY_EVENT_MASK)
            focusTraversalKeysEnabled = false
            isVisible = true
        }

        override fun processKeyEvent(e: KeyEvent) {
            model.processKeyEvent(e)
            this.text = model.shortcutText
        }
    }
}