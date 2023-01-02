@file:Suppress("NAME_SHADOWING")

package com.intellij.remoterobot.utils

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.Fixture
import com.intellij.remoterobot.stepsProcessing.step
import java.awt.event.KeyEvent
import java.time.Duration


inline fun Fixture.keyboard(function: Keyboard.() -> Unit) {
    Keyboard(remoteRobot).function()
}

inline fun RemoteRobot.keyboard(function: Keyboard.() -> Unit) {
    Keyboard(this).function()
}

open class Keyboard(private val remoteRobot: RemoteRobot) {
    @JvmOverloads
    fun key(keyCode: Int, waitAfter: Duration = Duration.ofMillis(200)) = step("'${KeyEvent.getKeyText(keyCode)}'") {
        remoteRobot.runJs("robot.pressAndReleaseKey($keyCode)")
        Thread.sleep(waitAfter.toMillis())
    }

    fun hotKey(vararg keyCodes: Int) = step("'${keyCodes.contentToString()}'") {
        val keyCodes: IntArray = keyCodes
        remoteRobot.runJs(
            """
            ${keyCodes.joinToString("\n") { "robot.pressKey($it); Thread.sleep(100)" }}
            ${keyCodes.reversed().joinToString("\n") { "robot.releaseKey($it); Thread.sleep(100)" }}
        """
        )
    }


    fun enter() = key(KeyEvent.VK_ENTER)

    @JvmOverloads
    fun escape(waitAfter: Duration = Duration.ofMillis(200)) = key(KeyEvent.VK_ESCAPE, waitAfter)

    fun down() = key(KeyEvent.VK_DOWN)
    fun up() = key(KeyEvent.VK_UP)
    fun backspace() = key(KeyEvent.VK_BACK_SPACE)

    @JvmOverloads
    fun enterText(text: String, delayBetweenCharsInMs: Long = 50) = step("Typing '$text'") {
        remoteRobot.runJs(
            """
            let delay = $delayBetweenCharsInMs
            for (let c of "${text.replace("\"", "\\\"")}") {
                robot.type(c)
                if (delay > 0) {
                    Thread.sleep(delay)
                }
            }
        """
        )
    }

    fun pressing(key: Int, doWhilePress: Keyboard.() -> Unit) {
        remoteRobot.runJs("robot.pressKey($key)")
        this.doWhilePress()
        remoteRobot.runJs("robot.releaseKey($key)")
    }

    fun selectAll() {
        if (remoteRobot.isMac()) {
            hotKey(KeyEvent.VK_META, KeyEvent.VK_A)
        } else {
            hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
        }
    }

    fun doubleKeyPress(keyCode: Int) = step("double press keycode $keyCode") {
        if (remoteRobot.isMac()) {
            val keyName = when (keyCode) {
                KeyEvent.VK_SHIFT -> "shift"
                KeyEvent.VK_CONTROL -> "control"
                else -> throw IllegalStateException("unknown key name with code $keyCode")
            }
            doubleKeyPressOnMac(keyName)
        } else {
            remoteRobot.runJs(
                """
                robot.pressKey($keyCode)
                robot.releaseKey($keyCode)
                Thread.sleep(10)
                robot.releaseKey($keyCode)
                robot.pressKey($keyCode)
            """
            )
        }
    }

    private fun doubleKeyPressOnMac(keyName: String) {
        remoteRobot.runJs(
            """
            let command = "tell application \"System Events\"\n" +
                    "  key down {$keyName}\n" +
                    "  key up {$keyName}\n" +
                    "  delay 0.1\n" +
                    "  key down {$keyName}\n" +
                    "  key up {$keyName}\n" +
                    "end tell\n" +
                    "delay 1\n"
            Runtime.getRuntime().exec(["osascript", "-e", command])
        """
        )
    }
}