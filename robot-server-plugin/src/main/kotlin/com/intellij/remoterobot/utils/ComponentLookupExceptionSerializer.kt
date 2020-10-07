package com.intellij.remoterobot.utils

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.assertj.swing.exception.ComponentLookupException
import java.lang.reflect.Type

class ComponentLookupExceptionSerializer : JsonSerializer<ComponentLookupException> {
    override fun serialize(
        src: ComponentLookupException, typeOfSrc: Type, serializationContext: JsonSerializationContext
    ): JsonElement {
        return serializationContext.serialize(RuntimeException(src.message))
    }
}