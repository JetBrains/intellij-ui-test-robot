// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures.dataExtractor

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.fixtures.Fixture

class ExtractedData(private val fixture: Fixture) {
    private var _data: List<TextData>? = null

    operator fun get(text: String): RemoteText {
        if (_data == null) {
            reloadData()
        }
        try {
            return RemoteText(fixture, _data!!.first { it.text == text })
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find text '$text' in the '$fixture'\n${availableTexts()}")
        }
    }

    fun hasText(txt: String): Boolean {
        if (_data == null) {
            reloadData()
        }
        if (_data!!.any { it.text == txt }) {
            return true
        }
        reloadData()
        return _data!!.any { it.text == txt }
    }

    fun getOne(filter: (TextData) -> Boolean): RemoteText {
        if (_data == null) {
            reloadData()
        }
        try {
            return RemoteText(fixture, _data!!.first(filter))
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find text in the '$fixture'\n${availableTexts()}")
        }
    }

    fun getAll(): List<RemoteText> {
        if (_data == null) {
            reloadData()
        }
        return _data!!.map { RemoteText(fixture, it) }
    }

    fun getMany(filter: (TextData) -> Boolean): List<RemoteText> {
        reloadData()
        try {
            return _data!!.filter(filter).map { RemoteText(fixture, it) }
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find text in the '$fixture'\n${availableTexts()}")
        }
    }

    fun reloadData() {
        _data = fixture.extractData()
    }


    private fun availableTexts(): String = StringBuilder().apply {
        if (_data == null || _data.isNullOrEmpty()) {
            append("no texts\n")
            return@apply
        }
        append("available:\n")
        _data!!.forEach { append("${it.text}(${it.point.x},${it.point.y})\n") }
    }.toString()

}