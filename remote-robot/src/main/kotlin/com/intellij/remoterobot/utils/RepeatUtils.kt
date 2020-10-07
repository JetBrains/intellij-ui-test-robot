package com.intellij.remoterobot.utils

import java.time.Duration

fun waitForIgnoringError(
    duration: Duration = Duration.ofSeconds(5),
    interval: Duration = Duration.ofSeconds(2),
    errorMessage: String = "",
    condition: () -> Boolean
) {
    waitFor(duration, interval, errorMessage) {
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
    errorMessage: String = "",
    condition: () -> Boolean
) {
    waitFor(duration, interval, { errorMessage }, condition)
}

@JvmOverloads
fun waitFor(
    duration: Duration = Duration.ofSeconds(5),
    interval: Duration = Duration.ofSeconds(2),
    errorMessageSupplier: () -> String,
    condition: () -> Boolean
) {
    if (repeatInTime(duration, interval) { condition() }) return
    throw WaitForConditionTimeoutException(duration, errorMessageSupplier())
}

class WaitForConditionTimeoutException(duration: Duration, errorMessage: String): IllegalStateException("Exceeded timeout ($duration) for condition function ${if (errorMessage.isNotEmpty()) "($errorMessage)" else ""} ")

fun <R> waitFor(
    duration: Duration,
    interval: Duration = Duration.ofSeconds(2),
    errorMessage: String = "",
    functionWithCondition: () -> Pair<Boolean, R>
): R {
    var result: R? = null
    if (repeatInTime(duration, interval) {
            val (condition, functionResult) = functionWithCondition()
            result = functionResult
            return@repeatInTime condition
        }) {
        return result!!
    }
    throw WaitForConditionTimeoutException(duration, errorMessage)
}

fun repeatInTime(
    duration: Duration,
    interval: Duration = Duration.ofMillis(500),
    condition: () -> Boolean
): Boolean {
    val endTime = System.currentTimeMillis() + duration.toMillis()
    while (System.currentTimeMillis() < endTime) {
        if (condition())
            return true
        else {
            Thread.sleep(interval.toMillis())
        }
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


