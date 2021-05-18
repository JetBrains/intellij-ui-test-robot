package com.intellij.remoterobot.fixtures.dataExtractor.server

import com.intellij.remoterobot.utils.LruCache
import com.intellij.util.ui.UIUtil
import java.util.*
import java.util.function.Consumer

class TextToKeyCache {
    private val textToKeyMap = Collections.synchronizedMap(LruCache<String, MutableSet<String>>(10_000))

    init {
        val bundleClass = com.intellij.BundleBase::class.java
        val messageCallConsumerListField = try {
            bundleClass.getField("translationConsumerList")
        } catch (e: Throwable) {
            null
        }
        if (messageCallConsumerListField != null) {
            val list =
                messageCallConsumerListField.get(null) as MutableList<Consumer<com.intellij.openapi.util.Pair<String, String>>>
            list.add {
                val text = UIUtil.removeMnemonic(it.second)
                val key = it.first
                if (textToKeyMap.containsKey(text).not()) {
                    textToKeyMap[text] = mutableSetOf()
                }
                textToKeyMap[text]!!.add(key)
            }
        }
    }

    fun findKey(text: String): String? {
        var key: Set<String>? = textToKeyMap[text]
        if (key == null && text.endsWith("...")) {
            synchronized(textToKeyMap) {
                key = textToKeyMap[textToKeyMap.keys.firstOrNull { it.startsWith(text.split("...")[0]) }]
            }
        }
        if (key == null && text.contains("(")) {
            key = textToKeyMap[text.split("(")[0].trim()]
        }
        return key?.joinToString(" ") { it }
    }
}