package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.hasAnyComponent
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

open class JPopupMenuFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byType() = Locators.byType(JPopupMenu::class.java)

        @JvmStatic
        fun byContainsItem(item: String) = JPopupMenu::class.java.let { cls ->
            byXpath("""//div[@javaclass="${cls.name}" or contains(@classhierarchy, "${cls.name} ") or contains(@classhierarchy, " ${cls.name} ")][.//div[@text="$item"]]""")
        }
    }

    open fun select(vararg menuItems: String) = step("..select '${menuItems.contentToString()}'") {
        if (menuItems.isEmpty()) {
            return@step this
        }

        if (menuItems.size > 1) {
            jMenu(menuItems.first()).click()
        } else {
            menuItem(menuItems.single()).click()
            return@step this
        }

        var menu: JPopupMenuFixture? = null
        for (item in menuItems.slice(1 until menuItems.size - 1)) {
            waitFor(Duration.ofSeconds(5)) { remoteRobot.hasAnyComponent(byContainsItem(item)) }
            menu = findAll<JPopupMenuFixture>(byContainsItem(item)).singleOrNull()
                ?: remoteRobot.findAll<JPopupMenuFixture>(byContainsItem(item))
                    .distinctBy { it.locationOnScreen }
                    .singleOrNull() ?: error("JPopupMenu with item $item not found")
            menu.jMenu(item).click()
        }

        val activeMenu: JPopupMenuFixture = menu ?: this
        if (activeMenu.collectItems().distinct().contains(menuItems.last())) {
            activeMenu.menuItem(menuItems.last()).click()
        } else {
            remoteRobot.findAll<ComponentFixture>(
                Locators.byTypeAndProperties(JMenuItem::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to menuItems.last())
            ).distinctBy { it.locationOnScreen }.singleOrNull()?.click() ?: error("JMenuItem with text ${menuItems.last()} not found")
        }
        activeMenu
    }

    open fun collectItems(): List<String> = findAll<ComponentFixture>(Locators.byType(JMenuItem::class.java)).map { item ->
        item.extractData().joinToString("\t") { it.text }
    }

    @JvmOverloads
    fun jMenu(item: String, timeout: Duration = Duration.ofSeconds(5)): ComponentFixture = step("Search for menu item '$item'") {
        find(Locators.byTypeAndProperties(JMenu::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to item), timeout)
    }

    @JvmOverloads
    fun menuItem(item: String, timeout: Duration = Duration.ofSeconds(5)): ComponentFixture = step("Search for menu item with text '$item'") {
        find(Locators.byTypeAndProperties(JMenuItem::class.java, Locators.XpathProperty.ACCESSIBLE_NAME to item), timeout)
    }
}