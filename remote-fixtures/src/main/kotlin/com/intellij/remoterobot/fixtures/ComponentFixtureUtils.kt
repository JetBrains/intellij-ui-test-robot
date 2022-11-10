package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.SearchContext
import com.intellij.remoterobot.search.locators.Locator
import java.time.Duration

@JvmOverloads
fun SearchContext.component(locator: Locator, timeout: Duration = Duration.ofSeconds(5)): ComponentFixture {
    return find(ComponentFixture::class.java, locator, timeout)
}
fun SearchContext.components(locator: Locator): List<ComponentFixture> {
    return findAll(ComponentFixture::class.java, locator)
}