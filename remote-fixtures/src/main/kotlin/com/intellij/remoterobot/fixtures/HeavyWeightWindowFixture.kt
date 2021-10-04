package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.byXpath

@DefaultXpath(by = "HeavyWeightWindow type", xpath = "//div[@class='HeavyWeightWindow']")
@FixtureName("HeavyWeightWindow")
class HeavyWeightWindowFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ContainerFixture(remoteRobot, remoteComponent) {

    val itemsList
        get() = find<JListFixture>(byXpath("//div[@class='MyList']"))
}