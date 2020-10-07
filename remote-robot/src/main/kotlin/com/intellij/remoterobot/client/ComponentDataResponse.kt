package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName
import com.intellij.remoterobot.data.ComponentData

class ComponentDataResponse(
    @SerializedName("componentData") val componentData: ComponentData?,
    @SerializedName("exception") val exception: Throwable?
)