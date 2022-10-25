package com.intellij.remoterobot.recorder.ui.dialogs

import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.remoterobot.recorder.steps.common.CommonStepMeta
import com.intellij.remoterobot.recorder.steps.common.CommonStepModel
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import java.util.function.Supplier
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JTextField

internal class CreateNewCommonStepDialogWrapper(private val stepModel: CommonStepModel) : DialogWrapper(true) {
    init {
        init()
        title = RecordUITestFrame.UI_TEST_RECORDER_TITLE
    }

    override fun createCenterPanel(): JComponent? {
        val actionPanel = ActionPanel()

        return BorderLayoutPanel().apply {
            addToTop(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name", JTextField(stepModel.name).apply {
                        addPropertyChangeListener { stepModel.observableStepName.value = text }
                        stepModel.observableStepName.onChanged {
                            this.text = it
                        }
                    })
                    .addLabeledComponent("Step", JComboBox<CommonStepMeta>().apply {
                        val steps = CommonStepModel.getSteps()
                        steps.forEach { addItem(it) }
                        selectedItem = steps.first()
                        addItemListener {
                            stepModel.step = it.item as CommonStepMeta
                            actionPanel.showCommonStepSetting(stepModel)
                            revalidate()
                            repaint()
                        }
                        setRenderer { _, step, _, _, _ -> JBLabel(step.title) }
                    })
                    .panel
            )
            addToCenter(actionPanel.apply { showCommonStepSetting(stepModel) })
        }
    }


    internal class ActionPanel : BorderLayoutPanel() {
        private fun isUserInputValid(data: String, type: Class<*>): Boolean {
            var isValid = false
            when (type) {
                Int::class.java -> data.toIntOrNull()?.let { isValid = true }
                Long::class.java -> data.toLongOrNull()?.let { isValid = true }
                Double::class.java -> data.toDoubleOrNull()?.let { isValid = true }
                Float::class.java -> data.toFloatOrNull()?.let { isValid = true }
                String::class.java -> isValid = true
            }
            return isValid
        }

        fun showCommonStepSetting(stepModel: CommonStepModel) {
            removeAll()
            val form = FormBuilder.createFormBuilder()

            stepModel.step.parameters.forEach { parameter ->
                form.addLabeledComponent(parameter.name, JBTextField(parameter.value).apply {
                    document.whenTextChanged {
                        if (isUserInputValid(text, parameter.type)) {
                            parameter.value = text
                            stepModel.updateName()
                        }
                    }
                    ComponentValidator(stepModel.disposable).withValidator(
                        Supplier {
                            if (isUserInputValid(text, parameter.type).not()) ValidationInfo("Expected ${parameter.type.simpleName}", this) else null
                        })
                        .andRegisterOnDocumentListener(this)
                        .installOn(this)
                        .apply { revalidate() }
                })
            }
            addToCenter(form.panel)
            stepModel.updateName()
        }
    }
}