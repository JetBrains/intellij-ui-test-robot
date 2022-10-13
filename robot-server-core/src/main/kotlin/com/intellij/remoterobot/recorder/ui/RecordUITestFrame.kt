// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.DisposableEditorPanel
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.impl.IdeGlassPaneImpl
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.EditorTextField
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.util.ui.components.BorderLayoutPanel
import org.jetbrains.annotations.NonNls
import java.awt.Component
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

internal class RecordUITestFrame(private val model: RecordUITestModel) : JFrame(), Disposable {
    companion object {
        const val UI_TEST_RECORDER_TITLE = "UI Test Recorder"
        var isOpened: Boolean = false
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
        isOpened = true
        title = UI_TEST_RECORDER_TITLE
        setSize(800, 600)
        glassPane = IdeGlassPaneImpl(rootPane)
        add(BorderLayoutPanel().apply {
            addToCenter(OnePixelSplitter(0.2f).apply {
                firstComponent = stepsList(model)
                secondComponent = testEditor(model)
            })

        })
        isVisible = true
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                println("Stop recording")
                model.stop()
                isOpened = false
            }
        })
    }
}


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
        addListSelectionListener {
            model.select(selectedValue)
        }
        model.select(model.elements().toList().firstOrNull())
    }
    return ToolbarDecorator.createDecorator(generatorsList).apply {
        setPanelBorder(BorderFactory.createEmptyBorder())
        setScrollPaneBorder(BorderFactory.createEmptyBorder())
        setAddAction {
            //
            //val list = JBList<>()
            //JBPopupFactory.getInstance().createPopupChooserBuilder()
        }
    }.createPanel()
}

private fun testEditor(model: RecordUITestModel): JComponent {

    val editorFactory = EditorFactory.getInstance()
    val editorDocument = editorFactory.createDocument("")
    val myEditor = editorFactory.createEditor(editorDocument, ProjectManager.getInstance().defaultProject) as EditorEx
    val panel = DisposableEditorPanel(myEditor)

    Disposer.register(ProjectManager.getInstance().defaultProject, panel)

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
    model.onCodeUpdated {
        ApplicationManager.getApplication().runWriteAction {
            myEditor.document.setText(model.code)
        }
    }
    return panel
}