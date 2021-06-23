// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.fixtures

import com.intellij.remoterobot.RemoteCommand
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.ComponentContext
import com.intellij.remoterobot.data.ComponentData
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.data.TextData
import com.intellij.remoterobot.fixtures.dataExtractor.ExtractedData
import com.intellij.remoterobot.fixtures.dataExtractor.RemoteText
import org.intellij.lang.annotations.Language
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.util.function.Predicate
import javax.imageio.ImageIO

abstract class Fixture(
    val remoteRobot: RemoteRobot,
    val remoteComponent: RemoteComponent
) {
    val data by lazy { ExtractedData(this) }

    @Deprecated("findText()", ReplaceWith("findText(text)"))
    fun text(text: String): RemoteText = data[text]

    @Deprecated("findAllText()", ReplaceWith("findAllText()"))
    fun allText(): List<RemoteText> = data.getAll()

    @Deprecated("findAllText()", ReplaceWith("findAllText(text)"))
    fun allText(txt: String): List<RemoteText> = data.getMany { it.text == txt }

    @Deprecated("findAllText()", ReplaceWith("findAllText(filter)"))
    fun allText(filter: (TextData) -> Boolean): List<RemoteText> = data.getMany(filter)

    fun hasText(txt: String): Boolean = data.hasText(txt)

    fun hasText(filter: (TextData) -> Boolean): Boolean = data.getMany(filter).isNotEmpty()

    fun hasText(textPredicate: Predicate<TextData>): Boolean = data.getMany { textPredicate.test(it) }.isNotEmpty()

    fun findText(text: String): RemoteText {
        return findAllText(text).first()
    }

    fun findText(textPredicate: Predicate<TextData>): RemoteText {
        return findAllText(textPredicate).first()
    }

    fun findAllText() = findAllText(Predicate { true })

    fun findAllText(text: String) = findAllText(Predicate { it.text == text })

    fun findAllText(filter: (TextData) -> Boolean): List<RemoteText> {
        return data.getMany(filter)
    }

    fun findAllText(textPredicate: Predicate<TextData>): List<RemoteText> {
        return data.getMany { textPredicate.test(it) }
    }

    /**
     * Get component screenshot.
     * Use ImageIO.write() method with "png" formatName
     *
     * @param isPaintingMode false by default. Set true for rendering the component before capturing screenshot
     * @return BufferedImage specified as .png
     */
    fun getScreenshot(isPaintingMode: Boolean = false): BufferedImage {
        val bytes = remoteRobot.ideRobotClient.makeScreenshot(remoteComponent.id, isPaintingMode)
        return ImageIO.read(ByteArrayInputStream(bytes))
    }

    @Deprecated("Doesn't work from Java, consider to use `callJs`")
    @RemoteCommand
    fun execute(runInEdt: Boolean = false, function: ComponentContext.() -> Unit) {
        remoteRobot.execute(this, runInEdt, function)
    }

    @JvmOverloads
    @RemoteCommand
    fun runJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\n" +
                    "const global = global;\n\n"
        ) script: String, runInEdt: Boolean = false
    ) {
        remoteRobot.runJs(this, script, runInEdt)
    }

    @JvmOverloads
    @RemoteCommand
    fun <T : Serializable> callJs(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\nconst global = global;\n\n"
        ) script: String,
        runInEdt: Boolean = false
    ): T {
        return remoteRobot.callJs(this, script, runInEdt)
    }

    @Deprecated("Use runJs", ReplaceWith("runJs(script, runInEdt)"))
    @JvmOverloads
    @RemoteCommand
    fun execute(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\n" +
                    "const global = global;\n\n"
        ) script: String, runInEdt: Boolean = false
    ) {
        remoteRobot.runJs(this, script, runInEdt)
    }

    @Deprecated("Use callJs", ReplaceWith("callJs(script, runInEdt)"))
    @JvmOverloads
    @RemoteCommand
    fun <T : Serializable> retrieve(
        @Language(
            value = "JS",
            prefix = "const robot = robot;\nconst component = component;\nconst log = log;\nconst local = local;\n" +
                    "const global = global;\n\n"
        ) script: String,
        runInEdt: Boolean = false
    ): T {
        return remoteRobot.callJs(this, script, runInEdt)
    }

    @RemoteCommand
    fun retrieveData(): ComponentData {
        return remoteRobot.ideRobotClient.retrieveComponentData(remoteComponent.id)
    }

    fun extractData(): List<TextData> {
        return retrieveData().textDataList
    }

    val componentHashCode: Int
        get() {
            return callJs("component.hashCode();")
        }

    val hasFocus: Boolean
        get() {
            return callJs("component.hasFocus();", true)
        }

    val isShowing: Boolean
        get() {
            return callJs("component.isShowing();", true)
        }

    val isFocusOwner: Boolean
        get() {
            return callJs("component.isFocusOwner();", true)
        }

    val locationOnScreen: Point
        get() {
            return callJs("component.getLocationOnScreen();", true)
        }
}

