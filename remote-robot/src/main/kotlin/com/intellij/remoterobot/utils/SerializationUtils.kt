package com.intellij.remoterobot.utils

import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.URL

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

fun Class<*>.getClassByteArray(): ByteArray? {
    val classUrl: URL = this.classLoader.getResource(this.name.replace(".", "/") + ".class")
    return IOUtils.toByteArray(classUrl)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> ByteArray.deserialize(): T? {
    if (this.isEmpty()) return null
    return inputStream().use { inputStream ->
        ObjectInputStream(inputStream).use {
            it.readObject()
        }
    } as T
}
