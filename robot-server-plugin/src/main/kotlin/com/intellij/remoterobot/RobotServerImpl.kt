// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot

import com.intellij.remoterobot.client.FindByXpathRequest
import com.intellij.remoterobot.data.*
import com.intellij.remoterobot.data.js.ExecuteScriptRequest
import com.intellij.remoterobot.encryption.Encryptor
import com.intellij.remoterobot.encryption.EncryptorFactory
import com.intellij.remoterobot.services.IdeRobot
import com.intellij.remoterobot.services.xpath.XpathDataModelCreator
import com.intellij.remoterobot.services.xpath.convertToHtml
import com.intellij.remoterobot.utils.ComponentLookupExceptionSerializer
import com.intellij.remoterobot.utils.serializeToBytes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import org.assertj.swing.exception.ComponentLookupException
import java.text.DateFormat

class RobotServerImpl {
    private val ideRobot: IdeRobot by lazy { IdeRobot() }
    private val serverPort by lazy { System.getProperty("robot-server.port")?.toIntOrNull() ?: 8580 }
    private val encryptor: Encryptor by lazy { EncryptorFactory().getInstance() }

    fun startServer() {
        embeddedServer(Netty, port = serverPort, configure = {
            connectionGroupSize = 1
            workerGroupSize = 1
            callGroupSize = 1
        }) {
            install(DefaultHeaders)
            install(ContentNegotiation) {
                gson {
                    setDateFormat(DateFormat.LONG)
                    setPrettyPrinting()
                    registerTypeAdapter(ComponentLookupException::class.java, ComponentLookupExceptionSerializer())
                }
            }

            routing {
                get("/hello") {
                    call.respond("Hello from idea")
                }

                get("/highlight") {
                    val parameters = call.parameters
                    val x = parameters["x"]?.toInt() ?: throw IllegalArgumentException("x is missed")
                    val y = parameters["y"]?.toInt() ?: throw IllegalArgumentException("y is missed")
                    val width = parameters["width"]?.toInt() ?: throw IllegalArgumentException("width is missed")
                    val height = parameters["height"]?.toInt() ?: throw IllegalArgumentException("height is missed")
                    ideRobot.highlight(x, y, width, height)
                    call.respond("ok")
                }

                static {
                    resources("static")
                }

                post("/component") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({ ideRobot.find(lambdaContainer = lambda) }) { result ->
                        FindComponentsResponse(
                            elementList = listOf(result.data!!), log = result.logs, time = result.time
                        )
                    }
                }
                post("/{id}/component") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.find(
                            containerId = id,
                            lambdaContainer = lambda
                        )
                    }) { result ->
                        FindComponentsResponse(
                            elementList = listOf(result.data!!),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                post("/xpath/component") {
                    val request = call.receive<FindByXpathRequest>()
                    call.dataResultRequest({
                        ideRobot.findByXpath(request.xpath)
                    }) { result ->
                        FindComponentsResponse(
                            elementList = listOf(result.data!!), log = result.logs, time = result.time
                        )
                    }
                }
                post("/xpath/{id}/component") {
                    val request = call.receive<FindByXpathRequest>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.findByXpath(
                            containerId = id,
                            xpath = request.xpath
                        )
                    }) { result ->
                        FindComponentsResponse(
                            elementList = listOf(result.data!!),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }

                post("/xpath/components") {
                    val request = call.receive<FindByXpathRequest>()
                    call.dataResultRequest({
                        ideRobot.findAllByXpath(request.xpath)
                    }) { result ->
                        FindComponentsResponse(
                            elementList = result.data!!, log = result.logs, time = result.time
                        )
                    }
                }
                post("/xpath/{id}/components") {
                    val request = call.receive<FindByXpathRequest>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.findAllByXpath(
                            containerId = id,
                            xpath = request.xpath
                        )
                    }) { result ->
                        FindComponentsResponse(elementList = result.data!!, log = result.logs, time = result.time)
                    }
                }

                post("/{id}/parentOfComponent") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.findParentOf(
                            containerId = id,
                            lambdaContainer = lambda
                        )
                    }) { result ->
                        FindComponentsResponse(
                            elementList = listOf(result.data!!),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                post("/components") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({ ideRobot.findAll(lambdaContainer = lambda) }) { result ->
                        FindComponentsResponse(
                            elementList = result.data!!, log = result.logs, time = result.time
                        )
                    }
                }
                post("/{id}/components") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.findAll(
                            containerId = id,
                            lambdaContainer = lambda
                        )
                    }) { result ->
                        FindComponentsResponse(elementList = result.data!!, log = result.logs, time = result.time)
                    }
                }

                get("/") {
                    hierarchy()
                }
                get("/hierarchy") {
                    hierarchy()
                }
                post("/execute") {
                    val lambda = call.receive<ObjectContainer>()
                    call.commonRequest {
                        ideRobot.doAction(lambda)
                    }
                }
                post("/{id}/execute") {
                    val lambda = call.receive<ObjectContainer>()
                    call.commonRequest {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.doAction(id, lambda)
                    }
                }
                post("/{id}/retrieveText") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.retrieveText(id, lambda)
                    }) { result ->
                        CommonResponse(message = result.data!!, log = result.logs, time = result.time)
                    }
                }
                post("/retrieveAny") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({ ideRobot.retrieveAny(lambda) }) { result ->
                        ByteResponse(
                            className = "",
                            bytes = result.data?.serializeToBytes() ?: ByteArray(0),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                post("/{id}/retrieveAny") {
                    val lambda = call.receive<ObjectContainer>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.retrieveAny(id, lambda)
                    }) { result ->
                        ByteResponse(
                            className = "",
                            bytes = result.data?.serializeToBytes() ?: ByteArray(0),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                post("/{id}/data") {
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        ideRobot.extractComponentData(id)
                    }) { result ->
                        ComponentDataResponse(componentData = result.data!!, log = result.logs, time = result.time)
                    }
                }

                // ----------------------------------------------------------------
                // JavaScript
                post("/js/execute") {
                    val request = call.receive<ExecuteScriptRequest>()
                    call.commonRequest {
                        val decryptedRequest = request.decrypt(encryptor)
                        ideRobot.doAction(decryptedRequest.script, decryptedRequest.runInEdt)
                    }
                }
                post("/{id}/js/execute") {
                    val request = call.receive<ExecuteScriptRequest>()
                    call.commonRequest {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        val decryptedRequest = request.decrypt(encryptor)

                        ideRobot.doAction(id, decryptedRequest.script, decryptedRequest.runInEdt)
                    }
                }
                post("/js/retrieveAny") {
                    val request = call.receive<ExecuteScriptRequest>()
                    call.dataResultRequest({
                        val decryptedRequest = request.decrypt(encryptor)
                        ideRobot.retrieveAny(decryptedRequest.script, decryptedRequest.runInEdt)
                    }) { result ->
                        ByteResponse(
                            className = "",
                            bytes = result.data?.serializeToBytes() ?: ByteArray(0),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                post("/{id}/js/retrieveAny") {
                    val request = call.receive<ExecuteScriptRequest>()
                    call.dataResultRequest({
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("empty id")
                        val decryptedRequest = request.decrypt(encryptor)
                        ideRobot.retrieveAny(id, decryptedRequest.script, decryptedRequest.runInEdt)
                    }) { result ->
                        ByteResponse(
                            className = "",
                            bytes = result.data?.serializeToBytes() ?: ByteArray(0),
                            log = result.logs,
                            time = result.time
                        )
                    }
                }
                //-----------------------------------------------------------------

            }
        }.start(wait = false)
    }

    private suspend fun PipelineContext<*, ApplicationCall>.hierarchy() {
        val doc = XpathDataModelCreator().create(null)
        call.respondText(doc.convertToHtml(), ContentType.Text.Html)
    }

    private companion object {
        suspend fun <T> ApplicationCall.dataResultRequest(
            code: () -> IdeRobot.Result<T>,
            responseCreator: (IdeRobot.Result<T>) -> Response
        ) {
            val response = try {
                val result = code()
                if (result.exception == null) {
                    responseCreator(result)
                } else {
                    CommonResponse(
                        ResponseStatus.ERROR,
                        result.exception.message,
                        result.time,
                        result.exception,
                        result.logs
                    )
                }
            } catch (e: Throwable) {
                CommonResponse(ResponseStatus.ERROR, e.message, 0L, e)
            }
            this.respond(response)
        }

        suspend fun ApplicationCall.commonRequest(code: () -> IdeRobot.Result<Unit>) {
            val response = try {
                val result = code()
                if (result.exception == null) {
                    CommonResponse(log = result.logs, time = result.time)
                } else {
                    CommonResponse(
                        ResponseStatus.ERROR,
                        result.exception.message,
                        result.time,
                        result.exception,
                        result.logs
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                CommonResponse(ResponseStatus.ERROR, e.message, 0L, e)
            }
            this.respond(response)
        }
    }
}
