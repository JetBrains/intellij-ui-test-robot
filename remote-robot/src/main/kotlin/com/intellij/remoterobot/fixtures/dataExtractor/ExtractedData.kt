// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures.dataExtractor

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.fixtures.Fixture

class ExtractedData(private val fixture: Fixture) {
    operator fun get(text: String): RemoteText {
        val data = reloadData()
        try {
            return RemoteText(fixture, data.first { it.text == text })
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find text '$text' in the '$fixture'\n${availableTexts(data)}")
        }
    }

    fun hasText(txt: String): Boolean {
        return reloadData().any { it.text == txt }
    }

    fun getOne(filter: (TextData) -> Boolean): RemoteText {
        val data = reloadData()
        try {
            return RemoteText(fixture, data.first(filter))
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find text in the '$fixture'\n${availableTexts(data)}")
        }
    }

    fun getAll(): List<RemoteText> {
        return reloadData().map { RemoteText(fixture, it) }
    }

    fun getMany(filter: (TextData) -> Boolean): List<RemoteText> {
        return reloadData().filter(filter).map { RemoteText(fixture, it) }
    }

    fun reloadData() = fixture.extractData()

    private fun availableTexts(data: List<TextData>): String = buildString {
        if (data.isNullOrEmpty()) {
            appendLine("no texts")
            return@buildString
        }
        appendLine("available:")
        data.forEach { appendLine("${it.text}(${it.point.x},${it.point.y})") }
    }

}