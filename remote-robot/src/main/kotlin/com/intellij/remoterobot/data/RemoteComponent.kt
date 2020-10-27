// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.data

import java.awt.Component
import java.io.Serializable

class RemoteComponent(
    val id: String,
    val className: String,
    val name: String? = null,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) : Serializable {

    constructor(id: String, component: Component) : this(
        id,
        component.javaClass.name,
        component.name,
        component.x,
        component.y,
        component.width,
        component.height
    )
}