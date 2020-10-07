package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName

class RetrieveResponse(
    @SerializedName("bytes") val data: ByteArray?,
    @SerializedName("exception") val exception: Throwable?,
    @SerializedName("message") val message: String?,
    @SerializedName("log") val log: String
)