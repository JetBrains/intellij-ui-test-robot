package com.intellij.remoterobot.recorder.steps.mouse

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.recorder.ui.ObservableField
import java.awt.Point

internal class MouseMoveOperation(
    private val model: MouseEventStepModel
) : MouseEventOperation {
    val where: ObservableField<Point?> = ObservableField<Point?>(null).apply { onChanged { model.updateName() } }
    val atText: ObservableField<TextData?> = ObservableField<TextData?>(null).apply { onChanged { model.updateName() } }

    override val name: String
        get() = buildString {
            append("Move mouse")
            if (where.value != null) {
                append("(${where.value})")
            }
            if (atText.value != null) {
                append(" to '${atText.value?.text}'")
            }
        }

    override fun getActionCode(): String = buildString {
        if (atText.value != null) {
            if (model.useBundleKeys && atText.value?.bundleKey != null) {
                append("findText(byKey(\"${atText.value?.bundleKey}\")).")
            } else {
                append("findText(\"${atText.value?.text}\").")
            }
        }
        append("moveMouse(")
        if (where.value != null) {
            append("Point(${where.value?.x}, ${where.value?.y})")
        }
        append(")")
    }
}