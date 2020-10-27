// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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