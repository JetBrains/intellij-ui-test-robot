package com.intellij.remoterobot.fixtures.dataExtractor

import com.intellij.remoterobot.data.TextData
import java.util.function.Predicate
import java.util.regex.Pattern

fun startsWith(startsWith: String) = Predicate<TextData> {
    it.text.startsWith(startsWith)
}

fun contains(contains: String) = Predicate<TextData> {
    it.text.contains(contains)
}


fun endsWith(endsWith: String) = Predicate<TextData> {
    it.text.endsWith(endsWith)
}

fun matches(regex: String) = Predicate<TextData> {
    Pattern.compile(regex).matcher(it.text).matches()
}