// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.stepsProcessing

interface StepProcessor {
    fun doBeforeStep(stepTitle: String)
    fun doOnSuccess(stepTitle: String)
    fun doOnFail(stepTitle: String, e: Throwable)
    fun doAfterStep(stepTitle: String)
}