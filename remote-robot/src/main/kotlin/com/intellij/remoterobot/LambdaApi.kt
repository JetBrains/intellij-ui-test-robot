// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.remoterobot.client.IdeRobotClient
import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.RobotContext
import com.intellij.remoterobot.data.pack
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.utils.deserialize
import java.io.Serializable

@Deprecated("Doesn't work from Java, consider to use JS execute")
interface LambdaApi {
    val ideRobotClient: IdeRobotClient

    @RemoteCommand
    fun execute(runInEdt: Boolean = false, action: RobotContext.() -> Unit) {
        ideRobotClient.execute(action.pack(runInEdt))
    }

    @RemoteCommand
    fun execute(fixture: Fixture, runInEdt: Boolean = false, action: ComponentContext.() -> Unit) {
        ideRobotClient.execute(fixture.remoteComponent.id, action.pack(runInEdt))
    }

    @RemoteCommand
    fun <T : Serializable> retrieveNullable(
        runInEdt: Boolean = false,
        function: RobotContext.() -> T?
    ): T? {
        return ideRobotClient.retrieve(function.pack(runInEdt))?.deserialize()
    }

    @RemoteCommand
    fun <T : Serializable> retrieve(
        runInEdt: Boolean = false,
        function: RobotContext.() -> T?
    ): T {
        return retrieveNullable(runInEdt, function)!!
    }

    @RemoteCommand
    fun <T : Serializable> retrieveNullable(
        element: Fixture,
        runInEdt: Boolean = false,
        function: ComponentContext.() -> T?
    ): T? {
        return ideRobotClient.retrieve(element.remoteComponent.id, function.pack(runInEdt))?.deserialize()
    }

    @RemoteCommand
    fun <T : Serializable> retrieve(
        element: Fixture,
        runInEdt: Boolean = false,
        function: ComponentContext.() -> T?
    ): T {
        return retrieveNullable(element, runInEdt, function)!!
    }

    @RemoteCommand
    fun <T : Serializable> retrieveList(
        runInEdt: Boolean = false,
        function: RobotContext.() -> List<T>
    ): List<T> {
        return ideRobotClient.retrieve(function.pack(runInEdt))?.deserialize()!!
    }

    @RemoteCommand
    fun <T : Serializable> retrieveList(
        element: Fixture,
        runInEdt: Boolean = false,
        function: ComponentContext.() -> List<T>
    ): List<T> {
        return ideRobotClient.retrieve(element.remoteComponent.id, function.pack(runInEdt))?.deserialize()!!
    }
}