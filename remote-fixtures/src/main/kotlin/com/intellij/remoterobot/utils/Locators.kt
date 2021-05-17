package com.intellij.remoterobot.utils

import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.search.locators.byXpath
import java.awt.Component

object Locators {

    enum class XpathProperty(val title: String) {
        ACCESSIBLE_NAME("@accessiblename"),
        JAVA_CLASS("@javaclass"),
        ICON("@defaulticon"),
        SIMPLE_CLASS_NAME("@class"),
        CLASS_HIERARCHY("@classhierarchy"),
        TEXT("@visible_text"),
        TOOLTIP("@tooltiptext")
    }

    fun <T: Component> byType(cls: Class<T>): Locator {
        return byType(cls.name)
    }

    fun byType(clsName: String): Locator {
        return byXpath(
            "by type $clsName",
            """//div[@javaclass="$clsName" or contains(@classhierarchy, "$clsName ") or contains(@classhierarchy, " $clsName ")]"""
        )
    }

    fun byProperties(
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        val allProperties = listOf(property, *properties)
        return byXpath(
            "by properties ${allProperties.joinToString(",") { "(${it.first.title}, ${it.second})" }}",
            "//div[${allProperties.joinToString(" and ") { "${it.first.title}=\"${it.second}\"" }}]"
        )
    }

    fun byPropertiesContains(
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        val allProperties = listOf(property, *properties)
        return byXpath(
            "by properties ${allProperties.joinToString(",") { "(${it.first.title}, ${it.second})" }}",
            "//div[${allProperties.joinToString(" and ") { "contains(${it.first.title},\"${it.second}\")" }}]"
        )
    }

    fun <T : Component> byTypeAndProperties(
        cls: Class<T>,
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        return byTypeAndProperties(cls.name, property, *properties)
    }

    fun byTypeAndProperties(
        clsName: String,
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        val allProperties = listOf(property, *properties)
        val joinedProperties = allProperties.joinToString(" and ") { "${it.first.title}=\"${it.second}\"" }.let {
            if (allProperties.isNotEmpty()) {
                " and $it"
            } else {
                it
            }
        }
        return byXpath(
            "by type $clsName properties ${allProperties.joinToString(",") { "(${it.first.title}, ${it.second})" }}",
            """//div[(@javaclass="$clsName" or contains(@classhierarchy, "$clsName ") or contains(@classhierarchy, " $clsName "))$joinedProperties]"""
        )
    }

    fun <T : Component> byTypeAndPropertiesContains(
        cls: Class<T>,
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        return byTypeAndPropertiesContains(cls.name, property, *properties)
    }

    fun byTypeAndPropertiesContains(
        clsName: String,
        property: Pair<XpathProperty, String>,
        vararg properties: Pair<XpathProperty, String>
    ): Locator {
        val allProperties = listOf(property, *properties)
        val joinedProperties =
            allProperties.joinToString(" and ") { "contains(${it.first.title}, \"${it.second}\")" }.let {
                if (allProperties.isNotEmpty()) {
                    " and $it"
                } else {
                    it
                }
            }
        return byXpath(
            "by type $clsName properties ${allProperties.joinToString(",") { "(${it.first.title}, ${it.second})" }}",
            """//div[(@javaclass="$clsName" or contains(@classhierarchy, "$clsName ") or contains(@classhierarchy, " $clsName "))$joinedProperties]"""
        )
    }
}