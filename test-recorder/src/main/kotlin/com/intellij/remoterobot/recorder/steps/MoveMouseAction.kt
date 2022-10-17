package com.intellij.remoterobot.recorder.steps

import java.awt.Point

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