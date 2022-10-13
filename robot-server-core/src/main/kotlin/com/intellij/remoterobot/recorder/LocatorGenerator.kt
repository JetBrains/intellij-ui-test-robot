// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.remoterobot.recorder

import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCache
import com.intellij.remoterobot.services.xpath.XpathDataModelCreator
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.awt.Component
import java.io.StringWriter
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


internal class LocatorGenerator {
    private val xPath = XPathFactory.newInstance().newXPath()
    private val hierarchyGenerator = XpathDataModelCreator(TextToKeyCache)


    fun generateXpath(targetComponent: Component): String {
        val hierarchy = hierarchyGenerator.create(null, targetComponent)
        val targetElement = hierarchy.findNodes("//div[@robot_target_element='true']").first()
        val locator = findLocator(hierarchy, targetElement)
        return locator ?: throw IllegalStateException("can't create locator:\n${targetElement.getNodeString()}")
    }

    private fun Document.findComponents(xpathExpression: String): List<Component> {
        val result = xPath.compile(xpathExpression).evaluate(this, XPathConstants.NODESET) as NodeList
        return (0 until result.length).mapNotNull { result.item(it).getUserData("component") as? Component }
    }

    private fun Document.findNodes(xpathExpression: String): List<Node> {
        val result = xPath.compile(xpathExpression).evaluate(this, XPathConstants.NODESET) as NodeList
        return (0 until result.length).mapNotNull { result.item(it) }
    }

    private val bestAttributes = listOf("accessiblename.key", "class", "text.key")
    private val nonLocalizedAttributes = listOf("accessiblename", "text")
    private val visibleTextKeysAttribute = "visible_text_keys"

    private fun findLocator(hierarchy: Document, element: Node): String? {
        val foundPairs = bestAttributes.mapNotNull { attribute ->
            element.attributes.getNamedItem(attribute)?.nodeValue?.takeIf { it.isNotEmpty() }
                ?.let { value -> attribute to value }
        }.toMap().toMutableMap()

        var locator = buildLocator(foundPairs)
        if (isValidLocator(locator, hierarchy, element)) {
            return locator
        }

        fun tryToAddAttribute(attribute: String): String? {
            element.attributes.getNamedItem(attribute)?.nodeValue?.takeIf { it.isNotEmpty() && it.contains("@").not() }
                ?.let { foundPairs[attribute] = it }
            locator = buildLocator(foundPairs)
            if (isValidLocator(buildLocator(foundPairs), hierarchy, element)) {
                return locator
            } else {
                foundPairs.remove(attribute)
                return null
            }
        }

        tryToAddAttribute(visibleTextKeysAttribute)?.let { return it }

        (0 until element.attributes.length)
            .mapNotNull { element.attributes.item(it)?.nodeName }
            .filter { bestAttributes.contains(it).not() }
            .filter { it.endsWith(".key") || it.endsWith("icon") }
            .forEach { tryToAddAttribute(it)?.let { locator -> return locator } }

        nonLocalizedAttributes.forEach { tryToAddAttribute(it)?.let { locator -> return locator } }
        return null
    }

    private fun isValidLocator(locator: String, hierarchy: Document, targetElement: Node): Boolean {
        val nodes = hierarchy.findNodes(locator)
        println("$locator = ${nodes.size}")
        return nodes.size == 1 && nodes.firstOrNull() == targetElement
    }

    private fun buildLocator(strictAttributes: Map<String, String>): String {
        val conditions = mutableListOf<String>()
        strictAttributes.forEach { (attribute, value) ->
            conditions.add("@$attribute='$value'")
        }
        return buildString {
            append("//div[")
            append(conditions.joinToString(" and ") { it })
            append("]")
        }
    }

    private fun Node.getNodeString(): String {
        try {
            val writer = StringWriter()
            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
            transformer.transform(DOMSource(this), StreamResult(writer))
            val output = writer.toString()
            return output.substring(output.indexOf("?>") + 2) //remove <?xml version="1.0" encoding="UTF-8"?>
        } catch (e: TransformerException) {
            e.printStackTrace()
        }
        return this.textContent
    }
}

