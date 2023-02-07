package com.intellij.remoterobot.recorder.steps.keyboard

import com.intellij.remoterobot.recorder.steps.GroupableStep


internal open class KeyboardGroupableStep: GroupableStep {
    override fun prefixCode(): String = "keyboard {"
    override fun postfixCode(): String = "}"
    override val groupId: String
        get() = "Keyboard Group"
}