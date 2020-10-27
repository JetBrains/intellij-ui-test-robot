// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

@file:JvmName("Locators")
package com.intellij.remoterobot.search.locators

import com.intellij.remoterobot.RemoteCommand
import com.intellij.remoterobot.data.RobotContext
import org.intellij.lang.annotations.Language
import java.awt.Component

sealed class Locator(val byDescription: String)

class LambdaLocator(
    byDescription: String,
    @RemoteCommand val searchFunction: RobotContext.(Component) -> Boolean
) : Locator(byDescription)


@Deprecated("Doesn't work from Java, consider to use XPATH locator")
fun byLambda(
    byDescription: String, @RemoteCommand searchFunction: RobotContext.(Component) -> Boolean
) = LambdaLocator(byDescription, searchFunction)

class XpathLocator(
    byDescription: String, @Language("XPath") val xpath: String
) : Locator(byDescription)

fun byXpath(@Language("XPath") xpath: String): Locator = XpathLocator(xpath, xpath)
fun byXpath(byDescription: String, @Language("XPath") xpath: String): Locator = XpathLocator(byDescription, xpath)