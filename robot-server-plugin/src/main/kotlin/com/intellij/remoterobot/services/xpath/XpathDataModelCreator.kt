// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.services.xpath

import com.intellij.remoterobot.services.dataExtractor.TextParser
import com.intellij.util.ReflectionUtil
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.edt.GuiTask
import org.assertj.swing.hierarchy.ComponentHierarchy
import org.assertj.swing.hierarchy.ExistingHierarchy
import org.intellij.lang.annotations.Language
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.Component
import java.awt.Container
import java.lang.reflect.Field
import java.util.*
import javax.swing.JComponent
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.absoluteValue

class XpathDataModelCreator : ComponentToDocument {

    companion object {

        private fun addComponent(
            doc: Document,
            parentElement: Element,
            hierarchy: ComponentHierarchy,
            component: Component
        ) {
            val element = createElement(doc, component)
            parentElement.appendChild(element)

            val allChildren = hierarchy.childrenOf(component)
            val filteredChildren = allChildren.filter(componentFilter)
            val exceptionChildren = if (System.getProperty("os.name").startsWith("mac", true)) {
                allChildren.filter {
                    it.javaClass.name.contains("DialogWrapperPeerImpl")
                }.flatMap { hierarchy.childrenOf(it) }.filter(componentFilter)
            } else {
                emptyList()
            }
            mutableListOf<Component>().apply {
                addAll(filteredChildren)
                addAll(exceptionChildren)
            }.sortedWith(ComponentOrderComparator).forEach {
                addComponent(doc, element, hierarchy, it)
            }
        }

        private fun createElement(doc: Document, component: Component): Element {

            val element = doc.createElement("div")

            component.fillElement(doc, element)

            element.setUserData("component", component, null)
            return element
        }


        private fun <C : Component> C.fillElement(doc: Document, element: Element) {
            val jClass = if (javaClass.isAnonymousClass) {
                javaClass.superclass.name
            } else {
                javaClass.name
            }.substringAfterLast(".").substringAfterLast("$")

            element.setAttribute("class", jClass)
            element.setAttribute("javaclass", javaClass.name)
            element.setAttribute("classhierarchy", getClassHierarchy(javaClass))

            val elementText = StringBuilder().apply { append(jClass).append(". ") }

            val allFields = mutableSetOf<Field>()
            allFields.addAll(Component::class.java.declaredFields.toList())
            var clazz: Class<*> = this::class.java
            while (true) {
                allFields.addAll(clazz.declaredFields.toList())
                if (clazz == Component::class.java) {
                    break
                }
                clazz = clazz.superclass
            }
            allFields
                .filter(fieldsFilter)
                .forEach { field ->

                    field.isAccessible = true
                    val attributeName = field.name.replace("$", "_").toLowerCase()
                    val value = field.get(this)?.toString()?.let {
                        if (it.contains(".svg")) {
                            return@let it
                                .substringBeforeLast(".svg")
                                .substringAfterLast("/")
                                .substringAfterLast("\\") + ".svg"
                        } else {
                            return@let it
                        }
                    }?.apply {
                        element.setAttribute(attributeName, this)
                    }
                    field.isAccessible = false
                    value?.apply {
                        if (textFieldsFilter(attributeName, value)) {
                            elementText.append("$attributeName: '$this'. ")
                        }
                    }
                }

            val accessibleName = try {
                accessibleContext?.accessibleName
            } catch (e: NullPointerException) {
                null
            }
            if (accessibleName != null) {
                element.setAttribute("accessiblename", accessibleName)
            }

            val tooltipText = getTooltipText(this)
            if (tooltipText != null) {
                element.setAttribute("tooltipText", tooltipText)
            }

            if (isShowing) {
                val visibleChildren = if (this is Container) {
                    this.components.filter { it.isVisible }.size
                } else {
                    0
                }
                if ((visibleChildren == 0 || jClass == "JBList")
                    && bounds.width > 0 && bounds.height > 0
                ) {
                    try {
                        val text = TextParser.parseComponent(this, true).let {
                            StringJoiner(" || ").apply { it.forEach { textData -> add(textData.text) } }.toString()
                        }
                        element.setAttribute("visible_text", text)
                        if (text.trim().isNotEmpty()) {
                            elementText.append("visible_text: '$text'. ")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
            if (isShowing) {
                element.addIcon(
                    "show", 17,
                    "show(${locationOnScreen.x}, ${locationOnScreen.y}, $width, $height)"
                )
                element.appendChild(doc.createTextNode(" "))
            }
            element.appendChild(doc.createTextNode(elementText.toString()))
            element.addIcon(
                "locator", 17,
                "new XpathEditor(parentElement).generatePath()"
            )

            val xpathEditor = doc.createElement("xpathEditor")
            xpathEditor.appendChild(doc.createElement("input").apply {
                setAttribute("type", "text")
                setAttribute("size", "100")
                setAttribute("oninput", "new XpathEditor(parentElement.parentElement).checkXpath()")
            })
            xpathEditor.appendChild(doc.createElement("label"))
            xpathEditor.setAttribute("class", "hidden")
            element.appendChild(xpathEditor)
        }

        private val textFieldsFilter: (String, String) -> Boolean = { name, value ->
            value.trim().isNotEmpty() && value.length <= 35 && (
                    name.contains("title")
                            || name.contains("name")
                            || name.endsWith("text")
                            || value.contains(".svg")
                            || name.endsWith("action")
                    )
                    && name != "hideactiontext"
        }

        private val fieldsFilter: (Field) -> Boolean = {
            (it.name.toLowerCase().contains("text")
                    || it.name.toLowerCase() == "id"
                    || it.name.toLowerCase().endsWith("name")
                    || it.name.toLowerCase().contains("action")
                    || it.name.toLowerCase().contains("icon")
                    || it.name.toLowerCase().contains("title")
                    || it.name.toLowerCase() == "visible"
                    || it.name.toLowerCase() == "enabled"
                    || it.name.toLowerCase().contains("caption")
                    )
                    && it.name.toLowerCase().contains("listener").not()
                    && it.name.toLowerCase().endsWith("context").not()

        }

        private val componentFilter: (Component) -> Boolean = {
            it.isVisible && it.isShowing
                    && it::class.java.simpleName != "Corner"
        }

        private fun getTooltipText(component: Component): String? = if (component is JComponent) {
            try {
                component.toolTipText ?: component.getClientProperty("JComponent.helpTooltip")?.let {
                    ReflectionUtil.getField(
                        Class.forName("com.intellij.ide.HelpTooltip"),
                        it,
                        String::class.java,
                        "title"
                    )
                }
            } catch (e: Throwable) {
                null
            }
        } else {
            null
        }

        private fun getClassHierarchy(clazz: Class<*>) = buildString {
            var cl = clazz.superclass
            while (cl != null) {
                if (length > 0) append(" -> ")
                append(cl.name)
                if (JComponent::class.java.name == cl.name) break
                cl = cl.superclass
            }
        }

        private object ComponentOrderComparator : Comparator<Component> {
            override fun compare(c1: Component, c2: Component): Int {
                val yDiff = c1.locationOnScreen.y - c2.locationOnScreen.y
                if (yDiff.absoluteValue > 10) {
                    return yDiff
                }
                return c1.locationOnScreen.x - c2.locationOnScreen.x
            }

        }
    }


    override fun create(component: Component?): Document {

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

        GuiActionRunner.execute(object : GuiTask() {
            override fun executeInEDT() {
                val hierarchy = ExistingHierarchy()
                val rootElement = doc.createElement("div")
                doc.appendChild(rootElement)
                val containers = if (component != null) {
                    hierarchy.childrenOf(component)
                } else {
                    hierarchy.roots()
                }
                containers.filter { it.isShowing || it.javaClass.name.endsWith("SharedOwnerFrame") }.forEach {
                    addComponent(doc, rootElement, hierarchy, it)
                }
            }
        })
        return doc
    }
}

fun Element.addIcon(iconName: String, size: Int, @Language("JavaScript") onClickFunction: String) {
    val doc = this.ownerDocument
    val icon = doc.createElement("img")
    icon.setAttribute("src", "img/$iconName.png")
    icon.setAttribute("width", size.toString())
    icon.setAttribute("height", size.toString())

    icon.setAttribute(
        "onclick",
        onClickFunction
    )
    appendChild(icon)
}
