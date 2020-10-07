package com.intellij.remoterobot.stepsProcessing

interface StepProcessor {
    fun doBeforeStep(stepTitle: String)
    fun doOnSuccess(stepTitle: String)
    fun doOnFail(stepTitle: String, e: Throwable)
    fun doAfterStep(stepTitle: String)
}