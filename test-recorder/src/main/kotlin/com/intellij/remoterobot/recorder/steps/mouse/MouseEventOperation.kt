package com.intellij.remoterobot.recorder.steps.mouse

internal sealed class MouseEventOperation {
    abstract val name: String
    abstract fun getActionCode(): String
}