package com.intellij.remoterobot.stepsProcessing


import com.intellij.remoterobot.utils.Color
import com.intellij.remoterobot.utils.color


class StepLogger(private val indentVal: String = "    ") : StepProcessor {
    private var indent = ThreadLocal.withInitial { 0 }

    private fun indents() = buildString {
        repeat(indent.get()) { append(indentVal) }
    }

    override fun doBeforeStep(stepTitle: String) {
        log.info(indents() + stepTitle)
        indent.set(indent.get().plus(1))
    }

    override fun doOnSuccess(stepTitle: String) {

    }

    override fun doOnFail(stepTitle: String, e: Throwable) {
        log.warn("${indents()}Failed on step: $stepTitle (${getClassFileNameAndMethod()})".color(Color.RED))
    }

    override fun doAfterStep(stepTitle: String) {
        indent.set(indent.get().minus(1))
    }

    private fun getClassFileNameAndMethod(): String {
        return Thread.currentThread().stackTrace[3]?.let { "${it.fileName}_${it.methodName}" } ?: "---"
    }
}