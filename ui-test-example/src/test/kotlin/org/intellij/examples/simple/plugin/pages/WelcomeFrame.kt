package org.intellij.examples.simple.plugin.pages

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.*
import com.intellij.remoterobot.fixtures.ActionLinkFixture
import com.intellij.remoterobot.search.locators.byXpath

fun RemoteRobot.welcomeFrame(function: WelcomeFrame.()-> Unit) {
    find(WelcomeFrame::class.java).apply(function)
}

@FixtureName("Welcome Frame")
@DefaultXpath("type", "//div[@class='FlatWelcomeFrame']")
class WelcomeFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {
    val createNewProjectLink
        get() = actionLink(byXpath("New Project","//div[@class='JButton' and @text='New Project']"))
    val moreActions
        get() = button(byXpath("More Action", "//div[@accessiblename='More Actions' and @class='ActionButton']"))

    val heavyWeightPopup
        get() = remoteRobot.find(ComponentFixture::class.java, byXpath("//div[@class='HeavyWeightWindow']"))
}