package com.intellij.remoterobot.utils

import com.intellij.remoterobot.SearchContext
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.search.locators.byXpath
import org.intellij.lang.annotations.Language
import java.time.Duration


@JvmOverloads
fun SearchContext.component(
    @Language("xpath") xpath: String,
    timeout: Duration = Duration.ofSeconds(15)
): ComponentFixture {
    return find(ComponentFixture::class.java, byXpath(xpath), timeout)
}
