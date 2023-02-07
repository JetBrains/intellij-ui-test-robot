package com.intellij.remoterobot.recorder.steps

internal interface GroupableStep {
    fun prefixCode(): String
    fun postfixCode(): String

    val groupId: String

    fun isTheSameGroup(step: Any?) : Boolean {
        return step != null && step is GroupableStep && step.groupId == step.groupId
    }
}