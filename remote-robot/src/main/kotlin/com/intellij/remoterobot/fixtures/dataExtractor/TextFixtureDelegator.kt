package com.intellij.remoterobot.fixtures.dataExtractor

import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.fixtures.Fixture
import kotlin.reflect.KProperty

fun Fixture.txt(text: String, cached: Boolean = true): TextFixtureDelegator =
    TextFixtureDelegator(this, { it.text == text }, cached)

fun Fixture.txt(cached: Boolean = true, filter: (TextData) -> Boolean): TextFixtureDelegator =
    TextFixtureDelegator(this, filter, cached)

fun Fixture.allTxt(text: String, cached: Boolean = true): AllTextFixtureDelegator =
    AllTextFixtureDelegator(this, { it.text == text }, cached)

fun Fixture.allTxt(cached: Boolean = true, filter: (TextData) -> Boolean): AllTextFixtureDelegator =
    AllTextFixtureDelegator(this, filter, cached)



class TextFixtureDelegator(
    private val fixture: Fixture,
    private val filter: (TextData) -> Boolean,
    private val cached: Boolean
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): RemoteText {
        if (cached.not()) {
            fixture.data.reloadData()
        }
        return fixture.data.getOne(filter)
    }
}

class AllTextFixtureDelegator(
    private val fixture: Fixture,
    private val filter: (TextData) -> Boolean,
    private val cached: Boolean
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<RemoteText> {
        if (cached.not()) {
            fixture.data.reloadData()
        }
        return fixture.data.getMany(filter)
    }
}
