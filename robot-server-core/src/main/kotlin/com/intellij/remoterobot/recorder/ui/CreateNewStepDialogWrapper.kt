// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.remoterobot.data.TextData
import com.intellij.ui.components.JBLabel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame.Companion.UI_TEST_RECORDER_TITLE
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import org.assertj.swing.core.MouseButton
import java.awt.Component
import java.awt.Point
import java.util.*
import javax.swing.*

internal class CreateNewStepDialogWrapper(private val stepModel: StepModel) : DialogWrapper(true) {
    init {
        init()
        title = UI_TEST_RECORDER_TITLE
    }

    override fun createCenterPanel(): JComponent? {
        val actionPanel: ActionPanel = ActionPanel()

        return BorderLayoutPanel().apply {
            addToTop(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name", JTextField(stepModel.name).apply {
                        addPropertyChangeListener { stepModel.name = text }
                    })
                    .addLabeledComponent("Locator", JTextField(stepModel.locator).apply {
                        addPropertyChangeListener { stepModel.locator = text }
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

internal class StepModel(
    var name: String,
    val component: Component,
    val point: Point,
    var action: StepAction?,
    var locator: String,
    val texts: List<TextData>
) {

    fun generateStep(): String {
        val fixture = "ComponentFixture"
        return """
      |step("$name") {
      |   component("$locator")
      |     .${action?.getActionCode() ?: throw IllegalStateException("Action was not chosen")}
      |}
    """.trimMargin()
    }
}

internal enum class StepActionType(val text: String) {
    MouseClick("Click mouse"), MouseMove("Move mouse")
}

internal sealed class StepAction() {
    abstract val name: String
    abstract fun getActionCode(): String
}

internal class MouseClickAction(
    var button: MouseButton,
    var count: Int,
    var where: Point?,
    var atText: String?,
    var textKey: String?
) : StepAction() {
    override val name: String = buildString {
        append(button.name)
        append(" click($count) at ")
        when {
            atText != null -> append("text '$atText'")
            where != null -> append("point ${where?.x};${where?.y}")
            else -> append("center")
        }
    }

    override fun getActionCode(): String = buildString {
        if (atText != null) {
            if (textKey != null) {
                append("findText(byKey(\"$textKey\")).")
            } else {
                append("findText(\"$atText\").")
            }
        }
        if (button == MouseButton.LEFT_BUTTON && count == 1) {
            append("click")
        } else if (button == MouseButton.LEFT_BUTTON && count == 2) {
            append("doubleClick")
        } else if (button == MouseButton.RIGHT_BUTTON && count == 1) {
            append("rightClick")
        } else {
            throw NotImplementedError("Method for($button x $count) is not implemented in the RemoteRobot")
        }
        if (where != null) {
            append("(Point(${where?.x}, ${where?.y}))")
        } else {
            append("()")
        }
    }
}

internal class MoveMouseAction(
    var where: Point?,
    var atText: String?,
    var textKey: String?
) : StepAction() {
    override val name: String = buildString {
        append("Move mouse")
        if (where != null) {
            append("($where)")
        }
        if (atText != null) {
            append(" to '${atText}'")
        }
    }

    override fun getActionCode(): String = buildString {
        if (atText != null) {
            if (textKey != null) {
                append("findText(byKey(\"$textKey\")).")
            } else {
                append("findText(\"$atText\").")
            }
        }
        append("moveMouse(")
        if (where != null) {
            append("Point(${where!!.x}, ${where!!.y})")
        }
        append(")")
    }
}