// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.search.Finder
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

interface SearchContext {
    val finder: Finder

    private val _remoteRobot
        get() = when (this) {
            is RemoteRobot -> this
            is ContainerFixture -> remoteRobot
            else -> throw NotImplementedError("${this::class.java} is not implemented")
        }

    fun <T : Fixture> find(type: Class<T>): T {
        return find(type, Duration.ofSeconds(2))
    }

    fun <T : Fixture> find(type: Class<T>, timeout: Duration): T {
        val defaultLocator = type.annotations.filterIsInstance<DefaultXpath>().firstOrNull()
            ?: throw IllegalStateException("default locator is not defined for $type")
        return find(type, byXpath(defaultLocator.by, defaultLocator.xpath), timeout)
    }


    fun <T : Fixture> findAll(type: Class<T>): List<T> {
        val defaultLocator = type.annotations.filterIsInstance<DefaultXpath>().firstOrNull()
            ?: throw IllegalStateException("default locator is not defined for $type")
        return findAll(type, byXpath(defaultLocator.by, defaultLocator.xpath))
    }

    fun <T : Fixture> find(type: Class<T>, locator: Locator): T {
        return find(type, locator, Duration.ofSeconds(2))
    }

    fun <T : Fixture> find(type: Class<T>, locator: Locator, timeout: Duration): T {
        val name = type.annotations.filterIsInstance<FixtureName>().firstOrNull()?.name ?: type.simpleName

        return step("Search '$name' by '${locator.byDescription}'") {
            var foundComponents: List<RemoteComponent> = emptyList()
            waitFor(
                timeout,
                errorMessageSupplier = {
                    if (foundComponents.isEmpty()) {
                        "Failed to find '$name' by '${locator.byDescription}' in ${timeout.seconds}s"
                    } else {
                        "Found more than one '$name' by '${locator.byDescription}' in ${timeout.seconds}s. ${foundComponents.size} components found"
                    }
                }
            ) {
                foundComponents = finder.findMany(locator)
                foundComponents.size == 1
            }
            val component = foundComponents.single()

            return@step type.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java)
                .newInstance(_remoteRobot, component)
        }
    }

    fun <T : Fixture> findAll(type: Class<T>, locator: Locator): List<T> {
        val name = type.annotations.filterIsInstance<FixtureName>().firstOrNull()?.name ?: type.simpleName

        return step("Search many '$name's by '${locator.byDescription}'") {
            return@step finder.findMany(locator).map {
                type.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(_remoteRobot, it)
            }
        }
    }
}