// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.utils

import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

fun Serializable.serializeToBytes(): ByteArray = ByteArrayOutputStream().use { baos ->
    ObjectOutputStream(baos).use { oos ->
        oos.writeObject(this)
    }
    baos
}.toByteArray()

fun Function<*>.serializeToBytes(): ByteArray = ByteArrayOutputStream().use { baos ->
    ObjectOutputStream(baos).use { oos ->
        oos.writeObject(this)
    }
    baos
}.toByteArray()

@Suppress("UNCHECKED_CAST")
fun <T : Any> ByteArray.deserialize(): T? {
    if (this.isEmpty()) return null
    return inputStream().use { inputStream ->
        ObjectInputStream(inputStream).use {
            it.readObject()
        }
    } as T
}
