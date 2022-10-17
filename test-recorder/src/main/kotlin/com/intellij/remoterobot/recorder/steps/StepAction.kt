package com.intellij.remoterobot.recorder.steps

sealed class StepAction() {
    abstract val name: String
    abstract fun getActionCode(): String
}