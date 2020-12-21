// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.stepsProcessing

object StepWorker {
    val processors: List<StepProcessor>
        get() = _stepsProcessors
    private val _stepsProcessors = mutableListOf<StepProcessor>()

    @JvmStatic
    fun registerProcessor(stepProcessor: StepProcessor) = _stepsProcessors.add(stepProcessor)
}

fun step(text: String, stepBody: Runnable) {
    step(text) {
        stepBody.run()
    }
}

fun <O> step(text: String, stepBody: () -> O): O {
    StepWorker.processors.forEach { it.doBeforeStep(text) }
    try {
        val result = stepBody()
        StepWorker.processors.forEach { it.doOnSuccess(text) }
        return result
    } catch (e: Throwable) {
        StepWorker.processors.forEach { it.doOnFail(text, e) }
        throw e
    } finally {
        StepWorker.processors.forEach { it.doAfterStep(text) }
    }
}