package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName
import com.intellij.remoterobot.data.RemoteComponent

class FindComponentsResponse(
    @SerializedName("elementList") val components: List<RemoteComponent>?,
    @SerializedName("exception") val exception: Throwable?
)