package com.intellij.remoterobot.recorder.steps

import org.assertj.swing.core.MouseButton
import java.awt.Point

internal class MouseClickAction(
    var button: MouseButton,
    var count: Int,
    var where: Point?,
    var atText: String?,
    var textKey: String?
) : StepAction() {
    override val name: String = buildString {
        append(button.name)
        append(" click($count) at ")
        when {
            atText != null -> append("text '$atText'")
            where != null -> append("point ${where?.x};${where?.y}")
            else -> append("center")
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
        if (button == MouseButton.LEFT_BUTTON && count == 1) {
            append("click")
        } else if (button == MouseButton.LEFT_BUTTON && count == 2) {
            append("doubleClick")
        } else if (button == MouseButton.RIGHT_BUTTON && count == 1) {
            append("rightClick")
        } else {
            throw NotImplementedError("Method for($button x $count) is not implemented in the RemoteRobot")
        }
        if (where != null) {
            append("(Point(${where?.x}, ${where?.y}))")
        } else {
            append("()")
        }
    }
}