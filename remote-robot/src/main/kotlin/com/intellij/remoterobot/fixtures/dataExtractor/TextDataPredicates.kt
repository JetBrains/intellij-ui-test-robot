// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

fun byKey(key: String) = Predicate<TextData> { textData ->
    textData.bundleKey?.split(" ")?.any { it == key } ?: false
}