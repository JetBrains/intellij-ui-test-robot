package com.intellij.remoterobot.services.xpath

import org.w3c.dom.Document
import java.awt.Component

interface ComponentToDocument {

    fun create(component: Component?): Document
}