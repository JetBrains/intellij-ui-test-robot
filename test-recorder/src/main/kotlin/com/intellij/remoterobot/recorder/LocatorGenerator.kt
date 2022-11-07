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
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory


internal class LocatorGenerator {
    private val xPath = XPathFactory.newInstance().newXPath()
    private val hierarchyGenerator = XpathDataModelCreator(TextToKeyCache)


    fun generateXpath(targetComponent: Component): String {
        val hierarchy = hierarchyGenerator.create(null, targetComponent)
        val targetElement = hierarchy.findNodes("//div[@robot_target_element='true']").single()

        // has it unique straight locator?
        val straightLocator = findLocator(hierarchy, targetElement)
        if (straightLocator != null) return straightLocator

        // trying to find unique parent
        val multipleLocator = findLocator(hierarchy, targetElement, false)
        if (multipleLocator != null) {
            var parentLocator: String? = null
            var parentElement = targetElement.parentNode
            while (parentLocator == null && parentElement != null) {
                parentLocator = findLocator(hierarchy, parentElement)
                if (parentLocator == null) {
                    parentElement = parentElement.parentNode
                }
            }
            if (parentLocator != null) {
                val combinedLocator = "$parentLocator$multipleLocator"
                if (isValidLocator(combinedLocator, hierarchy, targetElement, true)) {
                    return combinedLocator
                }
            }
        }
        // then build the whole path from the first unique parent
        val paths = mutableListOf<String>()
        val chainOfNodes = ArrayDeque<Node>()
        var element: Node? = targetElement
        while (element != null) {
            val currentLocator = findLocator(hierarchy, element, isSingle = true)
            chainOfNodes.addFirst(element)
            if (currentLocator == null) {
                element = element.parentNode
            } else {
                break
            }
        }
        fun wholePathLocator() = "/" + paths.map { it.replace("//", "/") }.joinToString("") { it }
        fun testNewPathElement(locatorToTest: String, node: Node): Boolean {
            val locator = wholePathLocator() + locatorToTest.replace("//", "/")
            return isValidLocator(locator, hierarchy, node, isSingle = true)
        }
        chainOfNodes.forEach { node ->
            val currentLocator = findLocator(hierarchy, node, isSingle = true)
            if (currentLocator != null) {
                paths.add(currentLocator)
            } else {
                val locator =
                    findLocator(hierarchy, node, isSingle = false) ?: throw CantCreateLocatorException(targetElement)
                if (testNewPathElement(locator, node)) {
                    paths.add(locator)
                } else {
                    (1..countElements(locator, hierarchy)).forEach {
                        val indexedLocator = "$locator[$it]"
                        if (testNewPathElement(indexedLocator, node)) {
                            paths.add(indexedLocator)
                        }
                    }
                }
            }
        }
        val uniqueLocator = wholePathLocator()
        if (isValidLocator(uniqueLocator, hierarchy, targetElement, true)) return uniqueLocator

        throw CantCreateLocatorException(targetElement)
    }


    private fun Document.findNodes(xpathExpression: String): List<Node> {
        val result = xPath.compile(xpathExpression).evaluate(this, XPathConstants.NODESET) as NodeList
        return (0 until result.length).mapNotNull { result.item(it) }
    }

    private fun findLocator(hierarchy: Document, element: Node, isSingle: Boolean = true): String? {
        val foundPairs = mutableMapOf<String, String>()
        var locator: String

        fun tryToAddAttribute(attribute: String, removeIfNotFinal: Boolean = true): String? {
            element.attributes.getNamedItem(attribute)?.nodeValue?.takeIf { it.isNotEmpty() && it.contains("@").not() }
                ?.let { foundPairs[attribute] = it }
            locator = buildLocator(foundPairs)
            return if (isValidLocator(buildLocator(foundPairs), hierarchy, element, isSingle)) {
                locator
            } else {
                if (removeIfNotFinal) {
                    foundPairs.remove(attribute)
                }
                null
            }
        }
        val bestAttributes = listOf("accessiblename.key", "class", "text.key")
        bestAttributes.forEach {
            tryToAddAttribute(it, false)?.let { locator ->
                println("found best locator: $locator")
                return locator
            }
        }

        val visibleTextKeysAttribute = "visible_text_keys"
        tryToAddAttribute(visibleTextKeysAttribute)?.let { return it }

        (0 until element.attributes.length)
            .mapNotNull { element.attributes.item(it)?.nodeName }
            .filter { bestAttributes.contains(it).not() }
            .filter { it.endsWith(".key") || it.endsWith("icon") }
            .forEach { tryToAddAttribute(it)?.let { locator -> return locator } }

        val nonLocalizedAttributes = listOf("accessiblename", "text")
        nonLocalizedAttributes.forEach { tryToAddAttribute(it)?.let { locator -> return locator } }
        return null
    }

    private fun isValidLocator(locator: String, hierarchy: Document, targetElement: Node, isSingle: Boolean): Boolean {
        val nodes = try {
            hierarchy.findNodes(locator)
        } catch (e: XPathExpressionException) {
            emptyList()
        }
        return if (isSingle) {
            nodes.size == 1 && nodes.single() == targetElement
        } else {
            nodes.isNotEmpty() && nodes.any { it == targetElement }
        }
    }

    private fun countElements(locator: String, hierarchy: Document): Int {
        val nodes = try {
            hierarchy.findNodes(locator)
        } catch (e: XPathExpressionException) {
            emptyList()
        }
        return nodes.size
    }

    private fun buildLocator(strictAttributes: Map<String, String>): String {
        val conditions = mutableListOf<String>()
        strictAttributes.forEach { (attribute, value) ->
            if (attribute.endsWith(".key") || attribute.endsWith("_keys")) {
                val shortValue = value.split(" ").firstOrNull() ?: value
                conditions.add("contains(@$attribute, '$shortValue')")
            } else {
                conditions.add("@$attribute='$value'")
            }
        }
        return buildString {
            append("//div[")
            append(conditions.joinToString(" and ") { it })
            append("]")
        }
    }
}

internal class CantCreateLocatorException(node: Node) :
    IllegalStateException("can't create locator for:\n${node.getNodeString()}") {
    companion object {
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
}