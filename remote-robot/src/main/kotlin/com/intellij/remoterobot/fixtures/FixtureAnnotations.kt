package com.intellij.remoterobot.fixtures

import org.intellij.lang.annotations.Language

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FixtureName(val name: String)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultXpath(val by: String, @Language("XPath") val xpath: String)