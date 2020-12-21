// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.remoterobot.client.IdeRobotApi
import com.intellij.remoterobot.client.IdeRobotClient
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.data.RobotContext
import com.intellij.remoterobot.data.pack
import com.intellij.remoterobot.encryption.Encryptor
import com.intellij.remoterobot.encryption.EncryptorFactory
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.search.Finder
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.utils.DefaultHttpClient
import com.intellij.remoterobot.utils.waitFor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.intellij.lang.annotations.Language
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.awt.Component
import java.time.Duration

@DslMarker
annotation class RemoteCommand


class RemoteRobot @JvmOverloads constructor(
    robotServerUrl: String,
) : SearchContext, JavaScriptApi, LambdaApi {

    // See retrofit rest calls in the logs for debug
    // https://stackoverflow.com/questions/45646188/how-can-i-debug-my-retrofit-api-call
    val okHttpClient: OkHttpClient = if (System.getProperty("debug-retrofit")?.equals("enable") ?: false) {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()
    } else {
        DefaultHttpClient.client
    }

    override val ideRobotClient = IdeRobotClient(
        Retrofit.Builder().baseUrl(robotServerUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(IdeRobotApi::class.java)
    )
    override val encryptor: Encryptor = EncryptorFactory().getInstance()

    override val finder = Finder(ideRobotClient)

    // Kotlin only Searching

    inline fun <reified T : Fixture> find(locator: Locator, timeout: Duration = Duration.ofSeconds(2)): T =
        find(T::class.java, locator, timeout)

    inline fun <reified T : Fixture> findAll(locator: Locator): List<T> = findAll(T::class.java, locator)

    inline fun <reified T : Fixture> find(timeout: Duration = Duration.ofSeconds(2)): T =
        find(T::class.java, timeout)

    inline fun <reified T : Fixture> findAll(): List<T> = findAll(T::class.java)


    fun isMac(): Boolean = os.startsWith("mac", true)

    fun isWin(): Boolean = os.startsWith("windows", true)

    fun isLinux(): Boolean = os.startsWith("linux", true)

    val os: String
        get() = callJs("com.intellij.openapi.util.SystemInfo.OS_NAME")


    // only for internal test project
    // ================================================================================

    @Deprecated(
        "use finder",
        ReplaceWith(
            "find<T>(byLambda(\"some lambda\", findBy))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> find(noinline findBy: RobotContext.(c: Component) -> Boolean): T {
        return find(null, findBy)
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "findAll<T>(byLambda(\"some lambda\", findBy))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findAll(noinline findBy: RobotContext.(c: Component) -> Boolean): List<T> {
        return findAll(null, findBy)
    }


    @Deprecated(
        "use finder",
        ReplaceWith(
            "find<T>(byXpath(xpath))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byXpath"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findByXpath(@Language("XPath") xpath: String): T {
        return findByXpath(null, xpath)
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "container.find<T>(byXpath(xpath), duration)",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byXpath"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findByXpathWithTimeout(
        @Language("XPath") xpath: String,
        container: Fixture? = null,
        duration: Duration = Duration.ofSeconds(5)
    ): T {
        waitFor(duration) {
            val components = findAllByXpath<ComponentFixture>(container, xpath)
            if (components.isEmpty()) {
                Thread.sleep(500)
            }
            components.isNotEmpty()
        }
        return findByXpath(container, xpath)
    }


    @Deprecated(
        "use finder",
        ReplaceWith(
            "findAll<T>(byXpath(xpath))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byXpath"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findAllByXpath(@Language("XPath") xpath: String): List<T> {
        return findAllByXpath(null, xpath)
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "find<T>(byLambda(\"some lambda\", findBy), duration)",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findWithTimeout(
        duration: Duration = Duration.ofSeconds(5),
        noinline findBy: RobotContext.(c: Component) -> Boolean
    ): T {
        return findWithTimeout(duration, null, findBy)
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "find<T>(byLambda(\"some lambda\", findBy), duration)",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findWithTimeout(
        duration: Duration = Duration.ofSeconds(5), container: Fixture?,
        noinline findBy: RobotContext.(c: Component) -> Boolean
    ): T {
        waitFor(duration) {
            findAll<T>(container, findBy).isNotEmpty()
        }
        return find(container, findBy)
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "container.find<T>(byLambda(\"some lambda\", findBy))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> find(
        container: Fixture?,
        noinline findBy: RobotContext.(c: Component) -> Boolean
    ): T {
        return if (container != null) {
            ideRobotClient.findByLambda(container.remoteComponent.id, findBy.pack())
        } else {
            ideRobotClient.findByLambda(findBy.pack())
        }.let {
            T::class.java.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(this, it)
        }
    }

    @RemoteCommand
    inline fun <reified T : Fixture> findParentOf(
        fixture: Fixture,
        noinline findBy: RobotContext.(c: Component) -> Boolean
    ): T {
        return ideRobotClient.findParentOf(fixture.remoteComponent.id, findBy.pack()).let {
            T::class.java.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(this, it)
        }
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "container.findAll<T>(byLambda(\"some lambda\", findBy))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byLambda"
        )
    )
    @RemoteCommand
    inline fun <reified T : Fixture> findAll(
        container: Fixture?,
        noinline findBy: RobotContext.(c: Component) -> Boolean
    ): List<T> {
        return if (container != null) {
            ideRobotClient.findAllByLambda(container.remoteComponent.id, findBy.pack())
        } else {
            ideRobotClient.findAllByLambda(findBy.pack())
        }.map {
            T::class.java.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(this, it)
        }
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "container.find<T>(byXpath(xpath))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byXpath"
        )
    )
    inline fun <reified T : Fixture> findByXpath(
        container: Fixture?,
        @Language("XPath") xpath: String
    ): T {
        return if (container != null) {
            ideRobotClient.findByXpath(container.remoteComponent.id, xpath)
        } else {
            ideRobotClient.findByXpath(xpath)
        }.let {
            T::class.java.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(this, it)
        }
    }

    @Deprecated(
        "use finder",
        ReplaceWith(
            "container.findAll<T>(byXpath(xpath))",
            "com.jetbrains.test.search.locators.LambdaLocatorKt.byXpath"
        )
    )
    inline fun <reified T : Fixture> findAllByXpath(
        container: Fixture?,
        @Language("XPath") xpath: String
    ): List<T> {
        return if (container != null) {
            ideRobotClient.findAllByXpath(container.remoteComponent.id, xpath)
        } else {
            ideRobotClient.findAllByXpath(xpath)
        }.map {
            T::class.java.getConstructor(RemoteRobot::class.java, RemoteComponent::class.java).newInstance(this, it)
        }
    }
}

