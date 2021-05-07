package com.intellij.remoterobot.utils

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.Locator

fun RemoteRobot.hasSingleComponent(locator: Locator) = finder.findMany(locator).size == 1

fun RemoteRobot.hasAnyComponent(locator: Locator) = finder.findMany(locator).isNotEmpty()

fun ContainerFixture.hasSingleComponent(locator: Locator) = finder.findMany(locator).size == 1

fun ContainerFixture.hasAnyComponent(locator: Locator) = finder.findMany(locator).isNotEmpty()

fun RemoteRobot.getIdeBuildNumber() =
    callJs<String>("com.intellij.openapi.application.ApplicationInfo.getInstance().getBuildNumber()")