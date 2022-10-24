// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.steps.mouse.MouseClickOperation
import com.intellij.remoterobot.recorder.steps.mouse.MouseMoveOperation
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepActionType
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.ui.components.JBLabel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.UI_TEST_RECORDER_TITLE
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import org.assertj.swing.core.MouseButton
import java.util.*
import javax.swing.*

internal class CreateNewMouseEventStepDialogWrapper(private val stepModel: MouseEventStepModel) : DialogWrapper(true) {
    init {
        init()
        title = UI_TEST_RECORDER_TITLE
    }

    override fun createCenterPanel(): JComponent? {
        val actionPanel = ActionPanel()

        return BorderLayoutPanel().apply {
            addToTop(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name", JTextField(stepModel.name).apply {
                        addPropertyChangeListener { stepModel.observableStepName.value = text }
                        stepModel.observableStepName.onChanged {
                            println(it)
                            this.text = it
                        }
                    })
                    .addLabeledComponent("Locator", JTextField(stepModel.xpath).apply {
                        addPropertyChangeListener { stepModel.xpath = text }
                    })
                    .addLabeledComponent("Action", JComboBox<MouseEventStepActionType>().apply {
                        MouseEventStepActionType.values().forEach { addItem(it) }
                        selectedItem = MouseEventStepActionType.MouseClick
                        addItemListener {
                            when (it.item) {
                                MouseEventStepActionType.MouseClick -> actionPanel.showMouseClickSetting(stepModel)
                                MouseEventStepActionType.MouseMove -> actionPanel.showMouseMoveSetting(stepModel)
                            }
                            revalidate()
                            repaint()
                        }
                        setRenderer { _, stepActionType, _, _, _ -> JBLabel(stepActionType.text) }
                    })
                    .panel
            )
            addToCenter(actionPanel.apply { showMouseClickSetting(stepModel) })
        }
    }


    internal class ActionPanel : BorderLayoutPanel() {
        fun showMouseClickSetting(stepModel: MouseEventStepModel) {
            val action = MouseClickOperation(stepModel)
            stepModel.operation.value = action
            removeAll()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Mouse Action", JComboBox<MouseButton>().apply {
                        addItem(MouseButton.LEFT_BUTTON)
                        addItem(MouseButton.RIGHT_BUTTON)
                        selectedItem = action.button
                        addActionListener { action.button.value = selectedItem as MouseButton }
                        setRenderer { _, button, _, _, _ -> JBLabel(button.name.lowercase(Locale.getDefault())) }
                    })
                    .addLabeledComponent("Click counts", JComboBox<Int>().apply {
                        addItem(1)
                        addItem(2)
                        selectedItem = action.count
                        addActionListener { action.count.value = selectedItem as Int }
                        setRenderer { _, count, _, _, _ -> JBLabel("$count") }
                    })
                    .addComponent(JCheckBox("Click at exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = action.where.value != null
                        addChangeListener {
                            if (isSelected) {
                                action.where.value = stepModel.point
                            } else {
                                action.where.value = null
                            }
                        }
                    })
                    .addLabeledComponent("Click at text", JComboBox<TextData>().apply {
                        addItem(null)
                        stepModel.texts.forEach { addItem(it) }
                        selectedItem = action.atText
                        addActionListener {
                            action.atText.value = (selectedItem as TextData?)?.text
                            action.textKey.value = (selectedItem as TextData?)?.bundleKey
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel("${text?.text ?: "---"}") }
                    })
                    .panel
            )
        }

        fun showMouseMoveSetting(stepModel: MouseEventStepModel) {
            val action = MouseMoveOperation(stepModel)
            stepModel.operation.value = action
            removeAll()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addComponent(JCheckBox("Move to exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = action.where.value != null
                        addActionListener {
                            if (isSelected) {
                                action.where.value = stepModel.point
                            } else {
                                action.where.value = null
                            }
                        }
                    })
                    .addLabeledComponent("Move to text", JComboBox<TextData>().apply {
                        addItem(null)
                        stepModel.texts.forEach { addItem(it) }
                        selectedItem = action.atText
                        addActionListener {
                            action.atText.value = selectedItem as TextData?
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel("${text?.text ?: "---"}") }
                    })
                    .panel
            )
        }
    }
}