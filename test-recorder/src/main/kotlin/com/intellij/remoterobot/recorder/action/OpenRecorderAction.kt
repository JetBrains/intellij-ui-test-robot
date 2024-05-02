// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.remoterobot.recorder.RecorderService

internal class OpenRecorderAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        RecorderService.getInstance().openUI()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = RecorderService.getInstance().isOpened.not()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}

