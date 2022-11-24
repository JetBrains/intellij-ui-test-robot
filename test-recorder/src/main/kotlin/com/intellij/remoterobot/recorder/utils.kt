package com.intellij.remoterobot.recorder

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.remoterobot.data.TextData
import java.awt.Component
import javax.swing.AbstractButton
import javax.swing.text.JTextComponent

fun Component.generateName(textsOnComponent: List<TextData>): String {
    if (textsOnComponent.size in 1..3) {
        return textsOnComponent.joinToString(" ") { it.text }
    }
    val name: String? = when (this) {
        is AbstractButton -> "$text Button"
        is JTextComponent -> text?.let {
            if (it.length > 20) {
                it.substring(0, 20)
            } else {
                it
            }
        }

        else -> name
    }
    return name ?: javaClass.name.substringAfterLast(".").substringBefore("$")
}

fun Disposable.whenDisposed(action: () -> Unit) {
    Disposer.register(this, Disposable(action))
}