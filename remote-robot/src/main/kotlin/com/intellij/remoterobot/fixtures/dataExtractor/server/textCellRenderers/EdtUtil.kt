// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures.dataExtractor.server

import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.edt.GuiQuery

fun <ReturnType> computeOnEdt(query: () -> ReturnType): ReturnType? = GuiActionRunner.execute(object : GuiQuery<ReturnType>() {
    override fun executeInEDT(): ReturnType = query()
})
