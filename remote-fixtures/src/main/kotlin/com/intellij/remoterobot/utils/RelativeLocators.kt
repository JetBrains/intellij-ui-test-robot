@file:Suppress("DuplicatedCode")

package com.intellij.remoterobot.utils

import com.intellij.remoterobot.data.componentAs
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.fixtures.JLabelFixture
import com.intellij.remoterobot.search.locators.LambdaLocator
import com.intellij.remoterobot.search.locators.Locator


import java.awt.Component
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JLabel

enum class Direction { RIGHT, UNDER }

object RelativeLocators {

    inline fun <reified T : Component> byLabel(jLabel: JLabelFixture): Locator {

        // Check whether we have `labelFor` component and it can be used as `T`
        val labeledComponentHashCode = jLabel.remoteRobot.retrieveNullable(jLabel) {
            val labeledComponent = componentAs<JLabel>().labelFor ?: return@retrieveNullable null

            if (Class.forName(T::class.java.canonicalName).isAssignableFrom(labeledComponent.javaClass)) {
                return@retrieveNullable labeledComponent.hashCode()
            }
            return@retrieveNullable null
        }

        if (labeledComponentHashCode != null) {
            return LambdaLocator("label '${jLabel.value}'") {
                it.hashCode() == labeledComponentHashCode
            }
        }
        return byComponentNearby<T>(jLabel, Direction.RIGHT)
    }

    inline fun <reified T : Component> byComponentNearby(
        fixture: Fixture,
        axis: Direction = Direction.RIGHT
    ): Locator {
        val nearbyComponentRect = with(fixture.remoteComponent) {
            Rectangle(fixture.locationOnScreen, Dimension(width, height))
        }
        return when (axis) {
            Direction.UNDER -> {
                LambdaLocator("it is under '${fixture.remoteComponent.className.substringAfterLast(".")}'") {
                    it.isShowing
                            && Class.forName(T::class.java.canonicalName).isAssignableFrom(it.javaClass)
                            && nearbyComponentRect.x + nearbyComponentRect.width / 2 >= it.locationOnScreen.x
                            && nearbyComponentRect.x + nearbyComponentRect.width / 2 <= it.locationOnScreen.x + it.width
                            && nearbyComponentRect.y < it.locationOnScreen.y
                }
            }
            Direction.RIGHT -> {
                LambdaLocator("it is on right form '${fixture.remoteComponent.className.substringAfterLast(".")}'") {
                    it.isShowing
                            && Class.forName(T::class.java.canonicalName).isAssignableFrom(it.javaClass)
                            && nearbyComponentRect.y + nearbyComponentRect.height / 2 >= it.locationOnScreen.y
                            && nearbyComponentRect.y + nearbyComponentRect.height / 2 <= it.locationOnScreen.y + it.height
                            && nearbyComponentRect.x < it.locationOnScreen.x
                }
            }
        }
    }
}