package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.SearchContext
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.Finder
import com.intellij.remoterobot.search.locators.Locator
import java.time.Duration

@FixtureName("Container")
open class ContainerFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ComponentFixture(remoteRobot, remoteComponent), SearchContext {

    override val finder by lazy { Finder(remoteRobot.ideRobotClient, this) }


    inline fun <reified T : Fixture> find(locator: Locator, timeout: Duration = Duration.ofSeconds(2)): T =
        find(T::class.java, locator, timeout)

    inline fun <reified T : Fixture> findAll(locator: Locator): List<T> = findAll(T::class.java, locator)

    inline fun <reified T : Fixture> find(timeout: Duration = Duration.ofSeconds(2)): T =
        find(T::class.java, timeout)

    inline fun <reified T : Fixture> findAll(): List<T> = findAll(T::class.java)
}