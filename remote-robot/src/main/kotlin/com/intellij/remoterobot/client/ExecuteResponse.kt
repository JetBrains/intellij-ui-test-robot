package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName

class ExecuteResponse(
    @SerializedName("exception") val exception: Throwable?,
    @SerializedName("message") val message: String?,
    @SerializedName("log") val log: String?
)