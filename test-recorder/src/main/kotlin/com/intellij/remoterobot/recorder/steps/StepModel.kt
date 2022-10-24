package com.intellij.remoterobot.recorder.steps

internal interface StepModel {
    val name: String
    fun generateStep(): String
}