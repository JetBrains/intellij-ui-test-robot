// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.steps.*
import com.intellij.remoterobot.recorder.steps.MouseClickAction
import com.intellij.remoterobot.recorder.steps.MoveMouseAction
import com.intellij.remoterobot.recorder.steps.StepActionType
import com.intellij.ui.components.JBLabel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.UI_TEST_RECORDER_TITLE
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import org.assertj.swing.core.MouseButton
import java.util.*
import javax.swing.*

internal class CreateNewStepDialogWrapper(private val stepModel: StepModel) : DialogWrapper(true) {
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
                        addPropertyChangeListener { stepModel.name = text }
                    })
                    .addLabeledComponent("Locator", JTextField(stepModel.xpath).apply {
                        addPropertyChangeListener { stepModel.xpath = text }
                    })
                    .addLabeledComponent("Action", JComboBox<StepActionType>().apply {
                        StepActionType.values().forEach { addItem(it) }
                        selectedItem = StepActionType.MouseClick
                        addItemListener {
                            when (it.item) {
                                StepActionType.MouseClick -> actionPanel.showMouseClickSetting(stepModel)
                                StepActionType.MouseMove -> actionPanel.showMouseMoveSetting(stepModel)
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


    internal class ActionPanel() : BorderLayoutPanel() {
        fun showMouseClickSetting(stepModel: StepModel) {
            val action = MouseClickAction(MouseButton.LEFT_BUTTON, 1, null, null, null)
            stepModel.action = action
            removeAll()
            val mouseActions = mutableListOf<StepAction>()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Mouse Action", JComboBox<MouseButton>().apply {
                        addItem(MouseButton.LEFT_BUTTON)
                        addItem(MouseButton.RIGHT_BUTTON)
                        selectedItem = action.button
                        addPropertyChangeListener { action.button = selectedItem as MouseButton }
                        setRenderer { _, button, _, _, _ -> JBLabel(button.name.lowercase(Locale.getDefault())) }
                    })
                    .addLabeledComponent("Click counts", JComboBox<Int>().apply {
                        addItem(1)
                        addItem(2)
                        selectedItem = action.count
                        addPropertyChangeListener { action.count = selectedItem as Int }
                        setRenderer { _, count, _, _, _ -> JBLabel("$count") }
                    })
                    .addComponent(JCheckBox("Click at exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = action.where != null
                        addPropertyChangeListener {
                            if (isSelected) {
                                action.where = stepModel.point
                            } else {
                                action.where = null
                            }
                        }
                    })
                    .addLabeledComponent("Click at text", JComboBox<TextData>().apply {
                        addItem(null)
                        stepModel.texts.forEach { addItem(it) }
                        selectedItem = action.atText
                        addPropertyChangeListener {
                            action.atText = (selectedItem as TextData?)?.text
                            action.textKey = (selectedItem as TextData?)?.bundleKey
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel("${text?.text ?: "---"}") }
                    })
                    .panel
            )
        }

        fun showMouseMoveSetting(stepModel: StepModel) {
            val action = MoveMouseAction(null, null, null)
            stepModel.action = action
            removeAll()
            addToCenter(
                FormBuilder.createFormBuilder()
                    .addComponent(JCheckBox("Move to exact point(${stepModel.point.x}, ${stepModel.point.y})").apply {
                        isSelected = action.where != null
                        addPropertyChangeListener {
                            if (isSelected) {
                                action.where = stepModel.point
                            } else {
                                action.where = null
                            }
                        }
                    })
                    .addLabeledComponent("Move to text", JComboBox<TextData>().apply {
                        addItem(null)
                        stepModel.texts.forEach { addItem(it) }
                        selectedItem = action.atText
                        addPropertyChangeListener {
                            action.atText = (selectedItem as TextData?)?.text
                            action.textKey = (selectedItem as TextData?)?.bundleKey
                        }
                        setRenderer { _, text, _, _, _ -> JBLabel("${text?.text ?: "---"}") }
                    })
                    .panel
            )
        }
    }
}