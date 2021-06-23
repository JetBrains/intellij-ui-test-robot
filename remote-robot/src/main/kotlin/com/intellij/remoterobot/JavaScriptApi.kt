// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.remoterobot.client.IdeRobotClient
import com.intellij.remoterobot.data.js.ExecuteScriptRequest
import com.intellij.remoterobot.encryption.Encryptor
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.utils.deserialize
import org.intellij.lang.annotations.Language
import java.io.Serializable

interface JavaScriptApi {

    val ideRobotClient: IdeRobotClient
    val encryptor: Encryptor

    @RemoteCommand
    fun runJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\nconst global = global;\n\n"
        ) script: String
    ) {
        runJs(script, false)
    }

    @RemoteCommand
    fun runJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\nconst global = global;\n\n"
        ) script: String, runInEdt: Boolean
    ) {
        ideRobotClient.execute(ExecuteScriptRequest(script, runInEdt).encrypt(encryptor))
    }

    @RemoteCommand
    fun runJs(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\nconst global = global;\n\n"
        ) script: String
    ) {
        runJs(fixture, script, false)
    }

    @RemoteCommand
    fun runJs(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\nconst global = global;\n\n"
        ) script: String, runInEdt: Boolean
    ) {
        ideRobotClient.execute(fixture.remoteComponent.id, ExecuteScriptRequest(script, runInEdt).encrypt(encryptor))
    }

    @RemoteCommand
    fun <T : Serializable> callJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\nconst global = global;\n\n"
        ) script: String
    ): T {
        return callJs(script, false)
    }

    @RemoteCommand
    fun <T : Serializable> callJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\nconst global = global;\n\n"
        ) script: String,
        runInEdt: Boolean
    ): T {
        return ideRobotClient.retrieve(ExecuteScriptRequest(script, runInEdt).encrypt(encryptor))?.deserialize()!!
    }

    @RemoteCommand
    fun <T : Serializable> callJs(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\nconst global = global;\n\n"
        ) script: String
    ): T {
        return callJs(fixture, script, false)
    }

    @RemoteCommand
    fun <T : Serializable> callJs(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\nconst global = global;\n\n"
        ) script: String,
        runInEdt: Boolean
    ): T {
        return ideRobotClient.retrieve(
            fixture.remoteComponent.id,
            ExecuteScriptRequest(script, runInEdt).encrypt(encryptor)
        )?.deserialize()!!
    }

    // ========================================================


    @Deprecated("Use runJs", ReplaceWith("runJs(script)"))
    @RemoteCommand
    fun execute(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String
    ) {
        runJs(script, false)
    }

    @Deprecated("Use runJs", ReplaceWith("runJs(script, runInEdt)"))
    @RemoteCommand
    fun execute(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String, runInEdt: Boolean
    ) {
        runJs(script, runInEdt)
    }

    @Deprecated("Use runJs", ReplaceWith("runJs(fixture, script)"))
    @RemoteCommand
    fun execute(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\n\n"
        ) script: String
    ) {
        runJs(fixture, script, false)
    }

    @Deprecated("Use runJs", ReplaceWith("runJs(fixture, script, runInEdt)"))
    @RemoteCommand
    fun execute(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\n\n"
        ) script: String, runInEdt: Boolean
    ) {
        runJs(fixture, script, runInEdt)
    }

    @Deprecated("Use callJs", ReplaceWith("callJs(script)"))
    @RemoteCommand
    fun <T : Serializable> retrieve(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String
    ): T {
        return callJs(script, false)
    }

    @Deprecated("Use callJs", ReplaceWith("callJs(script, runInEdt)"))
    @RemoteCommand
    fun <T : Serializable> retrieve(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String,
        runInEdt: Boolean
    ): T {
        return callJs(script, runInEdt)
    }

    @Deprecated("Use callJs", ReplaceWith("callJs(script)"))
    @RemoteCommand
    fun <T : Serializable> retrieve(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String
    ): T {
        return callJs(fixture, script, false)
    }

    @Deprecated("Use callJs", ReplaceWith("callJs(fixture, script, runInEdt)"))
    @RemoteCommand
    fun <T : Serializable> retrieve(
        fixture: Fixture,
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst log = log;\n\n"
        ) script: String,
        runInEdt: Boolean
    ): T {
        return callJs(fixture, script, runInEdt)
    }
}