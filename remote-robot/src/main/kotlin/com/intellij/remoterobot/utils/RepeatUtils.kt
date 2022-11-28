// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.utils

import com.intellij.remoterobot.stepsProcessing.step
import java.time.Duration

@JvmOverloads
fun waitForIgnoringError(
    duration: Duration = Duration.ofSeconds(5),
    interval: Duration = Duration.ofSeconds(2),
    description: String? = null,
    errorMessage: String = "",
    condition: () -> Boolean
) {
    waitFor(duration, interval, description, errorMessage) {
        try {
            condition()
        } catch (e: Throwable) {
            false
        }
    }
}

@JvmOverloads
fun waitFor(
    duration: Duration = Duration.ofSeconds(5),
    interval: Duration = Duration.ofSeconds(2),
    description: String? = null,
    errorMessage: String = "",
    condition: () -> Boolean
) {
    waitFor(duration, interval, description, { errorMessage }, condition)
}

@JvmOverloads
fun waitFor(
    duration: Duration = Duration.ofSeconds(5),
    interval: Duration = Duration.ofSeconds(2),
    description: String? = null,
    errorMessageSupplier: () -> String,
    condition: () -> Boolean
) {
    if (description != null) {
        return step("Waiting for $description") {
            if (repeatInTime(duration, interval, isLogEnabled = true) { condition() }) return@step
            throw WaitForConditionTimeoutException(duration, errorMessageSupplier())
        }
    }
    if (repeatInTime(duration, interval) { condition() }) return
    throw WaitForConditionTimeoutException(duration, errorMessageSupplier())
}

class WaitForConditionTimeoutException(duration: Duration, errorMessage: String) :
    IllegalStateException("Exceeded timeout ($duration) for condition function ${if (errorMessage.isNotEmpty()) "($errorMessage)" else ""} ")

fun <R> waitFor(
    duration: Duration,
    interval: Duration = Duration.ofSeconds(2),
    description: String? = null,
    errorMessage: String = "",
    functionWithCondition: () -> Pair<Boolean, R>
): R {
    var result: R? = null
    if (description != null) {
        return step("Waiting for $description") {
            if (repeatInTime(duration, interval, isLogEnabled = true) {
                    val (condition, functionResult) = functionWithCondition()
                    result = functionResult
                    return@repeatInTime condition
                }) {
                return@step result!!
            }
            throw WaitForConditionTimeoutException(duration, errorMessage)
        }
    } else {
        if (repeatInTime(duration, interval) {
                val (condition, functionResult) = functionWithCondition()
                result = functionResult
                return@repeatInTime condition
            }) {
            return result!!
        }
    }
    throw WaitForConditionTimeoutException(duration, errorMessage)
}

fun repeatInTime(
    duration: Duration,
    interval: Duration = Duration.ofMillis(500),
    isLogEnabled: Boolean = false,
    condition: () -> Boolean
): Boolean {
    val endTime = System.currentTimeMillis() + duration.toMillis()
    var now = System.currentTimeMillis()
    while (now < endTime) {
        val isHappen = if (isLogEnabled) {
            step("waiting(${endTime - now}ms left)") { return@step condition() }
        } else {
            condition()
        }
        if (isHappen)
            return true
        else {
            Thread.sleep(interval.toMillis())
        }
        now = System.currentTimeMillis()
    }
    return false
}

fun <O> tryTimes(
    tries: Int, onError: () -> Unit = {},
    onFinalError: () -> Unit = {},
    finalException: (Throwable) -> Exception,
    block: (tryNumber: Int) -> O
): O {
    var finalError: Throwable? = null
    for (i in 1..tries) {
        try {
            return block(i)
        } catch (e: Throwable) {
            onError()
            finalError = e
            Thread.sleep(500)
        }
    }
    val reason = finalError?.let { finalError } ?: IllegalStateException("Unknown error")
    onFinalError()
    throw finalException(reason)
}

fun bar(n: Int): String {
    val bar = StringBuilder()
    for (i in 1..n) {
        bar.append(".")
    }
    return bar.toString()
}

fun <O> attempt(tries: Int = 2, onError: () -> Unit = {}, block: (attemptNumber: Int) -> O): O =
    tryTimes(tries, onError, finalException = { OutOfAttemptsException(it) }, block = block)

class OutOfAttemptsException(e: Throwable?) : Exception(e)


