// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.client

import com.intellij.remoterobot.data.ComponentData
import com.intellij.remoterobot.data.ObjectContainer
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.data.js.ExecuteScriptRequest
import com.intellij.remoterobot.stepsProcessing.log
import org.intellij.lang.annotations.Language
import retrofit2.Response

class IdeRobotClient(private val ideRobotApi: IdeRobotApi) {
    fun findByLambda(lambda: ObjectContainer): RemoteComponent {
        return processFindResponse(ideRobotApi.findByLambda(lambda).execute()).single()
    }

    fun findByLambda(containerId: String, lambda: ObjectContainer): RemoteComponent {
        return processFindResponse(ideRobotApi.findByLambda(containerId, lambda).execute()).single()
    }

    fun findAllByLambda(lambda: ObjectContainer): List<RemoteComponent> {
        return processFindResponse(ideRobotApi.findAllByLambda(lambda).execute())
    }

    fun findAllByLambda(containerId: String, lambda: ObjectContainer): List<RemoteComponent> {
        return processFindResponse(ideRobotApi.findAllByLambda(containerId, lambda).execute())
    }

    fun findByXpath(xpath: String): RemoteComponent {
        return processFindResponse(ideRobotApi.findByXpath(FindByXpathRequest(xpath)).execute()).single()
    }

    fun findByXpath(containerId: String, xpath: String): RemoteComponent {
        return processFindResponse(ideRobotApi.findByXpath(containerId, FindByXpathRequest(xpath)).execute()).single()
    }

    fun findAllByXpath(xpath: String): List<RemoteComponent> {
        return processFindResponse(ideRobotApi.findAllByXpath(FindByXpathRequest(xpath)).execute())
    }

    fun findAllByXpath(containerId: String, @Language("XPath") xpath: String): List<RemoteComponent> {
        return processFindResponse(ideRobotApi.findAllByXpath(containerId, FindByXpathRequest(xpath)).execute())
    }

    fun findParentOf(componentId: String, lambda: ObjectContainer): RemoteComponent {
        return processFindResponse(ideRobotApi.findParentOf(componentId, lambda).execute()).single()
    }

    fun execute(lambda: ObjectContainer) {
        processExecuteResponse(ideRobotApi.execute(lambda).execute())
    }

    fun execute(componentId: String, lambda: ObjectContainer) {
        processExecuteResponse(ideRobotApi.execute(componentId, lambda).execute())
    }

    fun execute(request: ExecuteScriptRequest) {
        processExecuteResponse(ideRobotApi.execute(request).execute())
    }

    fun execute(componentId: String, request: ExecuteScriptRequest) {
        processExecuteResponse(ideRobotApi.execute(componentId, request).execute())
    }

    fun retrieve(lambda: ObjectContainer): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve(lambda).execute())
    }

    fun retrieve(containerId: String, lambda: ObjectContainer): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve(containerId, lambda).execute())
    }

    fun retrieve(executeScriptRequest: ExecuteScriptRequest): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve(executeScriptRequest).execute())
    }

    fun retrieve(containerId: String, executeScriptRequest: ExecuteScriptRequest): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve(containerId, executeScriptRequest).execute())
    }

    fun retrieveComponentData(componentId: String): ComponentData {
        return processRetrieveComponentDataResponse(ideRobotApi.retrieveComponentData(componentId).execute())
    }

    fun makeScreenshot(): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve().execute())
    }

    fun makeScreenshot(componentId: String, withPainting: Boolean): ByteArray? {
        return processRetrieveResponse(ideRobotApi.retrieve(componentId, withPainting).execute())
    }

    private fun processFindResponse(response: Response<FindComponentsResponse>): List<RemoteComponent> {
        check(response.isSuccessful) { "request failed" }
        val findResponse = response.body()!!
        if (findResponse.exception != null) {
            throw IdeaSideException(cause = findResponse.exception)
        }

        return checkNotNull(findResponse.components)
    }

    private fun processExecuteResponse(response: Response<ExecuteResponse>) {
        check(response.isSuccessful) { "request failed: ${response.message()}" }
        val executeResponse = response.body()!!
        remoteLog(executeResponse.log)
        if (executeResponse.exception != null) {
            throwIdeaSideError(executeResponse.message, executeResponse.exception)
        }
    }

    private fun processRetrieveResponse(response: Response<RetrieveResponse>): ByteArray? {
        check(response.isSuccessful) { "request failed: ${response.message()}" }
        val retrieveResponse = response.body()!!
        remoteLog(retrieveResponse.log)
        if (retrieveResponse.exception != null) {
            throwIdeaSideError(retrieveResponse.message, retrieveResponse.exception)
        }

        return retrieveResponse.data
    }

    private fun processRetrieveComponentDataResponse(response: Response<ComponentDataResponse>): ComponentData {
        check(response.isSuccessful) { "request failed: ${response.message()}" }
        val componentDataResponse = response.body()!!
        if (componentDataResponse.exception != null) {
            throw IdeaSideException(cause = componentDataResponse.exception)
        }

        return checkNotNull(componentDataResponse.componentData)
    }

    private fun remoteLog(remoteLog: String?) {
        if (remoteLog != null && remoteLog.isNotEmpty()) {
            log.info(remoteLog)
        }
    }

    private fun throwIdeaSideError(responseMessage: String?, exception: Throwable) {
        val message = exception.message ?: responseMessage
        throw IdeaSideException(message, exception)
    }
}