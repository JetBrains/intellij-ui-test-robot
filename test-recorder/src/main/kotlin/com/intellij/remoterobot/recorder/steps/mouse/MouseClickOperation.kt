package com.intellij.remoterobot.recorder.steps.mouse

import com.intellij.remoterobot.recorder.ui.ObservableField
import org.assertj.swing.core.MouseButton
import java.awt.Point

internal class MouseClickOperation(
    private val model: MouseEventStepModel
) : MouseEventOperation() {
    val button: ObservableField<MouseButton> =
        ObservableField(MouseButton.LEFT_BUTTON).apply { onChanged { model.updateName() } }
    val count: ObservableField<Int> = ObservableField(1).apply { onChanged { model.updateName() } }
    val where: ObservableField<Point?> = ObservableField<Point?>(null).apply { onChanged { model.updateName() } }
    val atText: ObservableField<String?> = ObservableField<String?>(null).apply { onChanged { model.updateName() } }
    val textKey: ObservableField<String?> = ObservableField<String?>(null).apply { onChanged { model.updateName() } }

    override val name: String
        get() = buildString {
            when (button.value) {
                MouseButton.RIGHT_BUTTON -> append("Right")
                MouseButton.LEFT_BUTTON -> append("Left")
                else -> {}
            }
            append(" ")
            when (count.value) {
                1 -> append("click")
                2 -> append("double click")
            }
            when {
                atText.value != null -> append(" at text '${atText.value}'")
                where.value != null -> append(" at point ${where.value?.x};${where.value?.y}")
            }
        }

    override fun getActionCode(): String = buildString {
        if (atText.value != null) {
            if (textKey.value != null) {
                append("findText(byKey(\"${textKey.value}\")).")
            } else {
                append("findText(\"${atText.value}\").")
            }
        }
        if (button.value == MouseButton.LEFT_BUTTON && count.value == 1) {
            append("click")
        } else if (button.value == MouseButton.LEFT_BUTTON && count.value == 2) {
            append("doubleClick")
        } else if (button.value == MouseButton.RIGHT_BUTTON && count.value == 1) {
            append("rightClick")
        } else {
            throw NotImplementedError("Method for(${button.value} x ${count.value}) is not implemented in the RemoteRobot")
        }
        if (where.value != null) {
            append("(Point(${where.value?.x}, ${where.value?.y}))")
        } else {
            append("()")
        }
    }
}