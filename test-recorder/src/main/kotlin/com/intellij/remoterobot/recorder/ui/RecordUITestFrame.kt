// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.DisposableEditorPanel
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.impl.IdeGlassPaneImpl
import com.intellij.remoterobot.recorder.steps.common.CommonStepModel
import com.intellij.remoterobot.recorder.steps.keyboard.TextHotKeyStepModel
import com.intellij.remoterobot.recorder.steps.keyboard.TextTypingStepModel
import com.intellij.remoterobot.recorder.steps.mouse.MouseEventStepModel
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewCommonStepDialogWrapper
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewHotKeyDialogWrapper
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewMouseEventStepDialogWrapper
import com.intellij.remoterobot.recorder.ui.dialogs.CreateNewTypingDialogWrapper
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.EditorTextField
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.CheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.components.BorderLayoutPanel
import org.jetbrains.annotations.NonNls
import java.awt.Component
import java.awt.event.*
import javax.swing.*


internal class RecordUITestFrame(private val model: RecordUITestModel, onClose: () -> Unit) : JFrame(), Disposable {
    companion object {
        const val UI_TEST_RECORDER_TITLE = "UI Test Recorder"

        fun isThisFromRecordTestFrame(targetComponent: Component): Boolean {
            var component: Component? = targetComponent
            while (component != null) {
                if (component is JFrame && component.title == UI_TEST_RECORDER_TITLE) return true
                if (component is JDialog && component.title == UI_TEST_RECORDER_TITLE) return true

                component = component.parent
            }
            return false
        }
    }

    init {
        title = UI_TEST_RECORDER_TITLE
        setSize(800, 600)
        glassPane = IdeGlassPaneImpl(rootPane)
        add(BorderLayoutPanel().apply {
            addToCenter(OnePixelSplitter(0.2f).apply {
                firstComponent = stepsList(model)
                secondComponent = testEditor(model)
            })
            addToBottom(settingsPanel(model))

        })
        isVisible = true
        Disposer.register(model.disposable, this)
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                model.stop()
                onClose()
            }
        })
    }
}

data class AddNewStepAction(val title: String, val action: () -> Unit)

private fun stepsList(model: RecordUITestModel): JComponent {
    val generatorsList = JBList(model).apply {
        setCellRenderer { _, value, _, _, _ ->
            @NonNls
            val text = buildString {
                append(value.name)
            }
            JBLabel(text)
        }
        selectionMode = ListSelectionModel.SINGLE_SELECTION
    }
    return ToolbarDecorator.createDecorator(generatorsList).apply {
        setPanelBorder(BorderFactory.createEmptyBorder())
        setScrollPaneBorder(BorderFactory.createEmptyBorder())
        setAddAction {
            JBPopupFactory.getInstance().createPopupChooserBuilder(
                listOf(
                    AddNewStepAction("Hotkey") {
                        val stepModel = TextHotKeyStepModel("", "")
                        if (CreateNewHotKeyDialogWrapper(stepModel).showAndGet()) {
                            model.addElement(stepModel)
                        }
                    },
                    AddNewStepAction("Keyboard typing") {
                        val stepModel = TextTypingStepModel(text = "")
                        if (CreateNewTypingDialogWrapper(stepModel).showAndGet()) {
                            model.addElement(stepModel)
                        }
                    },
                    AddNewStepAction("Step") {
                        val stepModel = CommonStepModel()
                        if (CreateNewCommonStepDialogWrapper(stepModel).showAndGet()) {
                            model.addElement(stepModel)
                        }
                    }
                )
            )
                .setRenderer { _, value, _, _, _ -> JBLabel(value.title) }
                .setItemChosenCallback { action -> action.action() }
                .createPopup()
                .show(it.preferredPopupPoint)
        }
        setEditAction {
            val selectedIdx = generatorsList.selectedIndex
            when (val step = model[selectedIdx]) {
                is MouseEventStepModel -> {
                    val editedStep = step.copy()
                    if (CreateNewMouseEventStepDialogWrapper(editedStep).showAndGet()) {
                        model[selectedIdx] = editedStep
                    }
                }

                is TextHotKeyStepModel -> {
                    val editedStep = step.copy()
                    if (CreateNewHotKeyDialogWrapper(editedStep).showAndGet()) {
                        model[selectedIdx] = editedStep
                    }
                }

                is TextTypingStepModel -> {
                    val editedStep = step.copy()
                    if (CreateNewTypingDialogWrapper(editedStep).showAndGet()) {
                        model[selectedIdx] = editedStep
                    }
                }

                is CommonStepModel -> {
                    val editedStep = step.copy()
                    if (CreateNewCommonStepDialogWrapper(editedStep).showAndGet()) {
                        model[selectedIdx] = editedStep
                    }
                }
            }
        }
    }.createPanel()
}

private fun testEditor(model: RecordUITestModel): JComponent {

    val editorFactory = EditorFactory.getInstance()
    val editorDocument = editorFactory.createDocument("")
    val myEditor = editorFactory.createEditor(editorDocument, ProjectManager.getInstance().defaultProject) as EditorEx
    val panel = DisposableEditorPanel(myEditor)

    Disposer.register(model.disposable, panel)

    EditorTextField.SUPPLEMENTARY_KEY.set(myEditor, true)
    myEditor.colorsScheme = EditorColorsManager.getInstance().globalScheme

    with(myEditor.settings) {
        isLineNumbersShown = true
        isWhitespacesShown = true
        isLineMarkerAreaShown = false
        isIndentGuidesShown = false
        isFoldingOutlineShown = false
        additionalColumnsCount = 0
        additionalLinesCount = 0
        isRightMarginShown = true
    }

    val pos = LogicalPosition(0, 0)
    myEditor.caretModel.moveToLogicalPosition(pos)
    myEditor.highlighter = EditorHighlighterFactory.getInstance()
        .createEditorHighlighter(LightVirtualFile("a.kt"), myEditor.colorsScheme, null)

    ApplicationManager.getApplication().runWriteAction {
        myEditor.document.setText(model.code)
    }
    model.onCodeUpdated { code ->
        ApplicationManager.getApplication().runWriteAction {
            myEditor.document.setText(code)
        }
    }
    return panel
}

private fun settingsPanel(model: RecordUITestModel): Component {
    return FormBuilder.createFormBuilder()
        .addComponent(
            CheckBox(
                "Use bundle keys in locators",
                model.useBundleKeys.value,
                "Turn it off if you don't want to run tests with different languages. Locators with bundle keys are less stable."
            ).apply {
                addChangeListener { model.useBundleKeys.value = isSelected }
            })
        .addComponent(
            CheckBox(
                "Record Mode",
                model.recordAllMode.value,
                "Record all mouse clicks"
            ).apply {
                addChangeListener { model.recordAllMode.value = isSelected }
            })
        .addComponent(JBLabel("Press 'Shift + click' on any Ide component to add mouse step").apply {
            model.recordAllMode.onChanged {
                text = when (it) {
                    true -> "Recording..."
                    false -> "Press 'Shift + click' on any Ide component to add mouse step"
                }
                icon = when (it) {
                    true -> AllIcons.Debugger.Db_set_breakpoint
                    false -> AllIcons.Debugger.Db_muted_breakpoint
                }
            }
        })
        .panel
}