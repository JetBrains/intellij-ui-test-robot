// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.steps.mouse.*
import com.intellij.remoterobot.recorder.steps.mouse.MouseClickOperation
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepActionType
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.remoterobot.recorder.steps.mouse.MouseMoveOperation
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.UI_TEST_RECORDER_TITLE
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.fields.IntegerField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import org.assertj.swing.core.MouseButton
import java.time.Duration
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JTextField

internal class CreateNewMouseEventStepDialogWrapper(private val stepModel: MouseEventStepModel) : DialogWrapper(true) {

    private companion object {
        val MouseEventOperation.actionType get() = when (this) {
            is MouseClickOperation -> MouseEventStepActionType.MouseClick
            is MouseMoveOperation -> MouseEventStepActionType.MouseMove
        }
    }

    init {
        init()
        title = UI_TEST_RECORDER_TITLE
    }

    override fun createCenterPanel(): JComponent {
        val actionPanel = ActionPanel()
        return BorderLayoutPanel().apply {
            addToTop(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name:", JTextField(stepModel.name).apply {
                        addPropertyChangeListener {
                            stepModel.observableStepName.value = text
                        }
                        stepModel.observableStepName.onChanged {
                            this.text = it
                        }
                    })
                    .addLabeledComponent("Locator:", JTextField(stepModel.xpath).apply {
                        addPropertyChangeListener { stepModel.xpath = text }
                    })
                    .addLabeledComponent("Timeout (sec):", IntegerField().apply {
                        stepModel.searchTimeout?.let {
                            value = it.seconds.toInt()
                        }
                        columns = 4
                        minValue = 0
                        valueEditor.addListener { stepModel.searchTimeout = Duration.ofSeconds(it.toLong()) }
                    })
                    .addLabeledComponent("Action:", JComboBox(MouseEventStepActionType.values()).apply {
                        selectedItem = stepModel.operation.actionType
                        addItemListener {
                            actionPanel.showMouseActionSettings(it.item as MouseEventStepActionType, stepModel)
                            revalidate()
                            repaint()
                        }
                        setRenderer { _, stepActionType, _, _, _ -> JBLabel(stepActionType.text) }
                    })
                    .panel
            )
            addToCenter(actionPanel.apply {
                showMouseActionSettings(stepModel.operation.actionType, stepModel)
            })
        }
    }

    internal class ActionPanel : BorderLayoutPanel() {
        fun showMouseActionSettings(actionType: MouseEventStepActionType, stepModel: MouseEventStepModel) {
            when (actionType) {
                MouseEventStepActionType.MouseClick -> showMouseClickSetting(stepModel)
                MouseEventStepActionType.MouseMove -> showMouseMoveSetting(stepModel)
            }
        }

        private fun showMouseClickSetting(stepModel: MouseEventStepModel) {
            val operation = stepModel.operation as? MouseClickOperation ?: MouseClickOperation().also { stepModel.operation = it }
            removeAll()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Mouse Button:", JComboBox<MouseButton>().apply {
                        addItem(MouseButton.LEFT_BUTTON)
                        addItem(MouseButton.RIGHT_BUTTON)
                        selectedItem = operation.button
                        addActionListener { stepModel.operation = operation.copy(button = selectedItem as MouseButton) }
                        setRenderer { _, button, _, _, _ ->
                            JBLabel(
                                button.name.lowercase(Locale.getDefault()).replace("_", " ")
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                            )
                        }
                    })
                    .addLabeledComponent("Click counts:", JComboBox<Int>().apply {
                        addItem(1)
                        addItem(2)
                        selectedItem = operation.count
                        addActionListener { stepModel.operation = operation.copy(count = selectedItem as Int) }
                        setRenderer { _, count, _, _, _ -> JBLabel("$count") }
                    })
                    .addComponent(JCheckBox("Click at exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = operation.where != null
                        addChangeListener {
                            if (isSelected) {
                                stepModel.operation = operation.copy(where = stepModel.point)
                            } else {
                                stepModel.operation = operation.copy(where = null)
                            }
                        }
                    })
                    .addLabeledComponent("Click at text:", JComboBox(arrayOf(null, *stepModel.texts.toTypedArray())).apply {
                        selectedItem = operation.atText
                        addActionListener {
                            val selectedItem = selectedItem as TextData?
                            stepModel.operation = operation.copy(atText = selectedItem?.text, textKey = selectedItem?.bundleKey)
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel(text?.text ?: "---") }
                    })
                    .panel
            )
        }

        private fun showMouseMoveSetting(stepModel: MouseEventStepModel) {
            val operation = stepModel.operation as? MouseMoveOperation ?: MouseMoveOperation().also { stepModel.operation = it }
            removeAll()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addComponent(JCheckBox("Move to exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = operation.where != null
                        addActionListener {
                            stepModel.operation = operation.copy(where = if (isSelected) stepModel.point else null)
                        }
                    })
                    .addLabeledComponent("Move to text", JComboBox(arrayOf(null, *stepModel.texts.toTypedArray())).apply {
                        selectedItem = operation.atText
                        addActionListener {
                            stepModel.operation = operation.copy(atText = selectedItem as TextData?)
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel(text?.text ?: "---") }
                    })
                    .panel
            )
        }
    }
}