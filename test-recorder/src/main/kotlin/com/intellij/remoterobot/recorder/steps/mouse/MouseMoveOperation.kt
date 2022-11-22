package com.intellij.remoterobot.recorder.steps.mouse

import com.intellij.remoterobot.data.TextData
import java.awt.Point

internal data class MouseMoveOperation(val where: Point? = null, val atText: TextData? = null) : MouseEventOperation {

    override val name: String
        get() = buildString {
            append("Move mouse")
            if (where != null) {
                append("(${where})")
            }
            if (atText != null) {
                append(" to '${atText.text}'")
            }
        }

    override fun generateActionCode(useBundleKeys: Boolean): String = buildString {
        if (atText!= null) {
            if (useBundleKeys && atText.bundleKey != null) {
                append("findText(byKey(\"${atText.bundleKey}\")).")
            } else {
                append("findText(\"${atText.text}\").")
            }
        }
        append("moveMouse(")
        if (where != null) {
            append("Point(${where.x}, ${where.y})")
        }
        append(")")
    }
}