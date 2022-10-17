package com.intellij.remoterobot.utils

import com.intellij.remoterobot.SearchContext
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.search.locators.byXpath
import org.intellij.lang.annotations.Language
import java.time.Duration


fun SearchContext.component(@Language("xpath") xpath: String): ComponentFixture {
    return find(ComponentFixture::class.java, byXpath( xpath), Duration.ofSeconds(15))
}
