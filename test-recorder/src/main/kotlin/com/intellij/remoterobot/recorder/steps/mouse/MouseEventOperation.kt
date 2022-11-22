package com.intellij.remoterobot.recorder.steps.mouse

internal sealed interface MouseEventOperation {
    val name: String
    fun generateActionCode(useBundleKeys: Boolean): String
}