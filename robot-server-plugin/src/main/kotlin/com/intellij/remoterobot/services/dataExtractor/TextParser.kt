// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.dataExtractor

import com.intellij.remoterobot.data.TextData
import com.intellij.ui.components.JBViewport
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.edt.GuiTask
import java.awt.Component
import java.awt.Point
import java.awt.image.BufferedImage
import javax.swing.JViewport

object TextParser {
    private val graphics = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics()

    fun parseComponent(component: Component, isEdt: Boolean): List<TextData> {
        val containerComponent = findContainerComponent(component) ?: return emptyList()
        val x = containerComponent.locationOnScreen.x - component.locationOnScreen.x
        val y = containerComponent.locationOnScreen.y - component.locationOnScreen.y
        val data = mutableListOf<TextData>()

        val g = DataExtractorGraphics2d(graphics, data, Point(x, y))
        parseData(g, containerComponent, isEdt)
        return data.distinct()
    }

    fun parseCellRenderer(component: Component, isEdt: Boolean): List<String> {
        val data = mutableListOf<String>()

        val g = CellReaderGraphics2d(graphics, data)
        parseData(g, component, isEdt)
        return data
    }

    private fun <G : ExtractorGraphics2d> parseData(g: G, component: Component, isEdt: Boolean) {
        if (isEdt) {
            component.paint(g)
        } else {
            GuiActionRunner.execute(object : GuiTask() {
                override fun executeInEDT() {
                    try {
                        component.paint(g)
                    } catch (e: NullPointerException) {
                        IllegalStateException(
                            "Text parsing error. Can't do paint on ${component::class.java.simpleName}",
                            e
                        ).printStackTrace()
                    }
                }
            })
        }
    }

    private fun findContainerComponent(component: Component): Component? {
        if (component.parent is JViewport) {
            return component.parent
        }
        return component
    }
}