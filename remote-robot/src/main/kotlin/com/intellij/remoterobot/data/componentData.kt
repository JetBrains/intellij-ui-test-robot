// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.data

import java.awt.Point
import java.io.Serializable

data class ComponentData(
    val textDataList: List<TextData>
)

data class TextData(val text: String, val point: Point, val bundleKey: String?) : Serializable
