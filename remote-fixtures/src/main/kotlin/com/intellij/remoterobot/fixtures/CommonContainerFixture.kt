package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.Locator
import java.time.Duration

/*
    for base fixtures we need at least this methods:

        component(locator: Locator, timeout: Duration): ComponentFixture { findWithTimeout.... }  - universal way of finding one component
        components(locator: Locator): List<ComponentFixture> { findAll.... }  - universal way of finding list of components

    and also we can have short calls for often using locators:
        component(text: String): ComponentFixture = component(ComponentFixture.byText(text)) - example of short method

 */

@Suppress("MemberVisibilityCanBePrivate")
open class CommonContainerFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent
) : ContainerFixture(remoteRobot, remoteComponent) {
    companion object {
        private val defaultFindTimeout = Duration.ofSeconds(5)
    }

    //----------------------------------------

    fun actionLink(locator: Locator, timeout: Duration = defaultFindTimeout): ActionLinkFixture =
        find(locator, timeout)


    fun actionLinks(locator: Locator): List<ActionLinkFixture> =
        findAll(locator)

    fun actionLink(text: String) = actionLink(ActionLinkFixture.byText(text))

    //----------------------------------------

    fun button(text: String): JButtonFixture = button(JButtonFixture.byText(text))

    fun button(locator: Locator, timeout: Duration = defaultFindTimeout): JButtonFixture {
        return find(locator, timeout)
    }

    fun buttons(locator: Locator): List<JButtonFixture> {
        return findAll(locator)
    }

    //----------------------------------------

    fun jLabel(locator: Locator, timeout: Duration = defaultFindTimeout): JLabelFixture =
        find(locator, timeout)

    fun jLabels(locator: Locator): List<JLabelFixture> =
        findAll(locator)

    fun jLabel(text: String) = jLabel(JLabelFixture.byText(text))

    //----------------------------------------

    fun textField(locator: Locator, timeout: Duration = defaultFindTimeout): JTextFieldFixture =
        find(locator, timeout)

    fun textFields(locator: Locator): List<JTextFieldFixture> =
        findAll(locator)

    fun textField(labelText: String, contains: Boolean = true) = textField(
        if (contains) {
            JTextFieldFixture.byLabel(jLabel(JLabelFixture.byContainsText(labelText)))
        } else {
            JTextFieldFixture.byLabel(jLabel(JLabelFixture.byText(labelText)))
        }
    )

    //----------------------------------------

    fun textArea(locator: Locator, timeout: Duration = defaultFindTimeout): JTextAreaFixture =
        find(locator, timeout)

    fun textAreas(locator: Locator): List<JTextAreaFixture> =
        findAll(locator)

    fun textArea() = textArea(JTextAreaFixture.byType())

    //----------------------------------------

    fun comboBox(locator: Locator, timeout: Duration = defaultFindTimeout): ComboBoxFixture =
        find(locator, timeout)

    fun comboBoxes(locator: Locator): List<ComboBoxFixture> =
        findAll(locator)

    fun comboBox(labelText: String, contains: Boolean = true) = comboBox(
        if (contains) {
            ComboBoxFixture.byLabel(jLabel(JLabelFixture.byContainsText(labelText)))
        } else {
            ComboBoxFixture.byLabel(jLabel(JLabelFixture.byText(labelText)))
        }
    )

    //----------------------------------------

    fun actionButton(
        locator: Locator = ActionButtonFixture.byType(),
        timeout: Duration = Duration.ofSeconds(5)
    ): ActionButtonFixture =
        find(locator, timeout)

    fun actionButtons(
        locator: Locator = ActionButtonFixture.byType()
    ): List<ActionButtonFixture> =
        findAll(locator)

    //----------------------------------------

    fun checkBox(locator: Locator, timeout: Duration = defaultFindTimeout): JCheckboxFixture =
        find(locator, timeout)

    fun checkBoxes(locator: Locator): List<JCheckboxFixture> =
        findAll(locator)

    fun checkBox(text: String, contains: Boolean = false) = checkBox(
        if (contains)
            JCheckboxFixture.byTextContains(text)
        else
            JCheckboxFixture.byText(text)
    )

    //----------------------------------------

    fun radioButton(locator: Locator, timeout: Duration = defaultFindTimeout): JRadioButtonFixture =
        find(locator, timeout)

    fun radioButtons(locator: Locator): List<JRadioButtonFixture> =
        findAll(locator)

    fun radioButton(text: String) = radioButton(JRadioButtonFixture.byText(text))

    //----------------------------------------

    fun jList(
        locator: Locator, timeout: Duration = defaultFindTimeout,
        func: JListFixture.() -> Unit = {}
    ): JListFixture =
        find<JListFixture>(locator, timeout).apply(func)

    fun jLists(locator: Locator): List<JListFixture> =
        findAll(locator)

    fun jLists() = jLists(JListFixture.byType())

    fun jList(func: JListFixture.() -> Unit = {}) = jList(JListFixture.byType(), func = func)

    //----------------------------------------

    fun jMenuBar(func: JMenuBarFixture.() -> Unit = {}) = find<JMenuBarFixture>(JMenuBarFixture.byType()).apply(func)

    fun jMenuBar(locator: Locator, func: JMenuBarFixture.() -> Unit = {}) = find<JMenuBarFixture>(locator).apply(func)

    //----------------------------------------

    fun jPopupMenus(): List<JPopupMenuFixture> = findAll(JPopupMenuFixture.byType())

    fun jPopupMenus(locator: Locator): List<JPopupMenuFixture> = findAll(locator)

    fun jPopupMenu(timeout: Duration = defaultFindTimeout, func: JPopupMenuFixture.() -> Unit = {}) =
        find<JPopupMenuFixture>(JPopupMenuFixture.byType(), timeout).apply(func)

    fun jPopupMenu(locator: Locator, timeout: Duration = defaultFindTimeout, func: JPopupMenuFixture.() -> Unit = {}) =
        find<JPopupMenuFixture>(locator, timeout).apply(func)

    //----------------------------------------

    fun browser(timeout: Duration = defaultFindTimeout): JCefBrowserFixture {
        val locator = if (remoteRobot.isMac()) {
            JCefBrowserFixture.macLocator
        } else {
            JCefBrowserFixture.canvasLocator
        }
        return find(locator, timeout)
    }

    fun browser(locator: Locator, timeout: Duration = defaultFindTimeout): JCefBrowserFixture =
        find(locator, timeout)

    //----------------------------------------

    fun textEditor(timeout: Duration = defaultFindTimeout): TextEditorFixture {
        return textEditor(TextEditorFixture.locator, timeout)
    }

    fun textEditor(locator: Locator, timeout: Duration = defaultFindTimeout): TextEditorFixture =
        find(locator, timeout)

    fun textEditors(): List<TextEditorFixture> {
        return textEditors(TextEditorFixture.locator)
    }

    fun textEditors(locator: Locator): List<TextEditorFixture> =
        findAll(locator)

    //----------------------------------------

    fun heavyWeightWindow(timeout: Duration = defaultFindTimeout): HeavyWeightWindowFixture =
        heavyWeightWindow(HeavyWeightWindowFixture.byXpath)

    fun heavyWeightWindow(locator: Locator, timeout: Duration = defaultFindTimeout): HeavyWeightWindowFixture =
        find(locator, timeout)

    fun heavyWeightWindows(): List<HeavyWeightWindowFixture> =
        heavyWeightWindows(HeavyWeightWindowFixture.byXpath)

    fun heavyWeightWindows(locator: Locator): List<HeavyWeightWindowFixture> =
        findAll(locator)
}