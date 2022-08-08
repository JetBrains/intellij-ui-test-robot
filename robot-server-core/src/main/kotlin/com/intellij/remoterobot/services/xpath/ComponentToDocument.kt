// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.xpath

import org.w3c.dom.Document
import java.awt.Component

interface ComponentToDocument {

    fun create(component: Component?): Document
}