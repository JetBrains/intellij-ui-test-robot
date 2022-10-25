package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.internal.statistic.eventLog.ShortcutDataProvider
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.recorder.steps.keyboard.TextHotKeyStepModel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import javax.swing.*

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

        private val hotkeys = getHotkeys()

        private val combobox = JComboBox<String>().apply {
            hotkeys.forEach { addItem(it.value) }
            isVisible = true
        }

        private val resultField = JTextField().apply {
            isVisible = true
        }

        private val addButton = JButton("Add").apply {
            addActionListener {
                // TODO: if macOs 'ctrl' -> 'meta'
                val intKey = hotkeys.entries.find { it.value == combobox.selectedItem }!!.key
                resultField.text += if (resultField.text.isNotEmpty()) " + ${combobox.selectedItem}" else "${combobox.selectedItem}"
                this@ActionPanel.model.text += if (this@ActionPanel.model.text.isNotEmpty()) ", $intKey" else "$intKey"
                this@ActionPanel.model.name = "Press hotkey '${resultField.text}'"
            }
        }

        fun showHotkeySetting() {
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addComponent(
                        JPanel().apply {
                            add("Hotkey", combobox)
                            add(addButton)
                        }
                    )
                    .addLabeledComponent("Result:", resultField)
                    .panel
            )
        }

        private fun getHotkeys() = ShortcutDataProvider::class.java.getDeclaredField("ourKeyCodes").let {
            it.isAccessible = true
            (it.get(ShortcutDataProvider::class.java) as Int2ObjectMap<String>).toSortedMap()
        }
    }
}