// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.EditorTextField
import javax.swing.JComponent

internal class UITestEditor : Disposable {

    private val myEditor: EditorEx

    fun getPanel(): JComponent = myEditor.component

    init {
        val editorFactory = EditorFactory.getInstance()
        val editorDocument = editorFactory.createDocument("")
        myEditor = editorFactory.createEditor(editorDocument, ProjectManager.getInstance().defaultProject) as EditorEx
        Disposer.register(ProjectManager.getInstance().defaultProject, this)
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
            .createEditorHighlighter(LightVirtualFile("NewUiTest.kt"), myEditor.colorsScheme, null)
    }

    override fun dispose() {
        EditorFactory.getInstance().releaseEditor(myEditor)
    }
}
