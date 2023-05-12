package com.intellij.remoterobot.services.lux

import com.intellij.remoterobot.data.ComponentData
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.dataExtractor.server.TextToKeyCache
import com.intellij.remoterobot.services.IdeRobot
import com.intellij.remoterobot.services.LambdaLoader
import com.intellij.remoterobot.services.js.RhinoJavaScriptExecutor
import com.intellij.remoterobot.services.xpath.XpathDataModelCreator
import org.w3c.dom.Document
import java.awt.Component
import java.io.Serializable

class LuxComponentParser(private val luxComponent: Component) {
    private val ideRobot: IdeRobot = IdeRobot(TextToKeyCache, RhinoJavaScriptExecutor(), LambdaLoader())
    fun hierarchy(): Document {
        return XpathDataModelCreator(TextToKeyCache).create(luxComponent)
    }
    fun findAll(xpath: String): IdeRobot.Result<List<RemoteComponent>> {
        return ideRobot.findAllByXpath(luxComponent, xpath)
    }
    fun callJs(componentId: String, script: String, runInEdt: Boolean): IdeRobot.Result<Serializable> {
        return ideRobot.retrieveAny(componentId, script, runInEdt)
    }
    fun getTextData(componentId: String): IdeRobot.Result<ComponentData> {
        return ideRobot.extractComponentData(componentId)
    }
}