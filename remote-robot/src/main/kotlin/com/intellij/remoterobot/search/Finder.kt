// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.search

import com.intellij.remoterobot.client.IdeRobotClient
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.data.pack
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.search.locators.LambdaLocator
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.search.locators.XpathLocator


class Finder(
    private val finderClient: IdeRobotClient,
    private val where: ContainerFixture? = null
) {

    fun findOne(locator: Locator): RemoteComponent {
        return when (locator) {
            is XpathLocator ->
                if (where != null) {
                    finderClient.findByXpath(where.remoteComponent.id, locator.xpath)
                } else {
                    finderClient.findByXpath(locator.xpath)
                }

            is LambdaLocator ->
                if (where != null) {
                    finderClient.findByLambda(where.remoteComponent.id, locator.searchFunction.pack(true))
                } else {
                    finderClient.findByLambda(locator.searchFunction.pack(true))
                }
        }
    }

    fun findMany(locator: Locator): List<RemoteComponent> {
        return when (locator) {
            is XpathLocator ->
                if (where != null) {
                    finderClient.findAllByXpath(where.remoteComponent.id, locator.xpath)
                } else {
                    finderClient.findAllByXpath(locator.xpath)
                }
            is LambdaLocator ->
                if (where != null) {
                    finderClient.findAllByLambda(where.remoteComponent.id, locator.searchFunction.pack(true))
                } else {
                    finderClient.findAllByLambda(locator.searchFunction.pack(true))
                }
        }
    }
}