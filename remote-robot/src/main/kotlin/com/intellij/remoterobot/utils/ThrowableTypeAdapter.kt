package com.intellij.remoterobot.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object ThrowableTypeAdapter : TypeAdapter<Throwable>() {

    override fun write(writer: JsonWriter, value: Throwable?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()

        // Include exception type name to give more context; for example, NullPointerException might
        // not have a message
        writer.name("type")
        writer.value(value::class.java.getSimpleName())

        writer.name("message")
        writer.value(value.message)

        val cause = value.cause
        if (cause != null) {
            writer.name("cause")
            write(writer, cause)
        }

        writer.endObject()
    }

    override fun read(reader: JsonReader): Throwable {
        throw UnsupportedOperationException()
    }
}