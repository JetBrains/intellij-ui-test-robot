package com.intellij.remoterobot.data

import java.awt.Point
import java.io.Serializable

data class ComponentData(
    val textDataList: List<TextData>
)

data class TextData(val text: String, val point: Point) : Serializable
