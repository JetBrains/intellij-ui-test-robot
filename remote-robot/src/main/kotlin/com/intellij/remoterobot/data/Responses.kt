// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.data

import com.intellij.remoterobot.data.ResponseStatus.SUCCESS

interface Response {
    val status: ResponseStatus
    val log: String
    val message: String?
    val time: Long
}

data class CommonResponse(
    override val status: ResponseStatus = SUCCESS,
    override val message: String? = null,
    override val time: Long,
    val exception: Throwable? = null,
    override val log: String = ""
) : Response

data class FindComponentsResponse(
    override val status: ResponseStatus = SUCCESS,
    override val message: String? = null,
    override val time: Long,
    val elementList: List<RemoteComponent>? = null,
    override val log: String = ""
) : Response

data class ByteResponse(
    override val status: ResponseStatus = SUCCESS,
    override val message: String? = null,
    override val time: Long,
    val className: String,
    val bytes: ByteArray,
    override val log: String = ""
) : Response

data class ComponentDataResponse(
    override val status: ResponseStatus = SUCCESS,
    override val message: String? = null,
    override val time: Long,
    val componentData: ComponentData,
    override val log: String = ""
) : Response


enum class ResponseStatus { SUCCESS, ERROR }