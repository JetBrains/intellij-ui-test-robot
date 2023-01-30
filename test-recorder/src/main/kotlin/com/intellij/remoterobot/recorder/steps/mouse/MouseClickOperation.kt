package com.intellij.remoterobot.recorder.steps.mouse

import org.assertj.swing.core.MouseButton
import java.awt.Point

internal data class MouseClickOperation(
    val button: MouseButton = MouseButton.LEFT_BUTTON,
    val count: Int = 1,
    val where: Point? = null,
    val atText: String? = null,
    val textKey: String? = null
) : MouseEventOperation {

    override val name: String
        get() = buildString {
            when (button) {
                MouseButton.RIGHT_BUTTON -> append("Right")
                MouseButton.LEFT_BUTTON -> append("Left")
                else -> error("Unknown button")
            }
            append(" ")
            when (count) {
                1 -> append("click")
                2 -> append("double click")
            }
            when {
                atText != null -> append(" at text '${atText}'")
            }
        }

    override fun generateActionCode(useBundleKeys: Boolean): String = buildString {
        if (atText != null) {
            if (useBundleKeys && textKey != null) {
                append("findText(byKey(\"${textKey}\")).")
            } else {
                append("findText(\"${atText}\").")
            }
        }
        if (button == MouseButton.LEFT_BUTTON && count == 1) {
            append("click")
        } else if (button == MouseButton.LEFT_BUTTON && count == 2) {
            append("doubleClick")
        } else if (button == MouseButton.RIGHT_BUTTON && count == 1) {
            append("rightClick")
        } else {
            append("doubleRightClick")
        }
        if (where != null) {
            append("(Point(${where.x}, ${where.y}))")
        } else {
            append("()")
        }
    }
}