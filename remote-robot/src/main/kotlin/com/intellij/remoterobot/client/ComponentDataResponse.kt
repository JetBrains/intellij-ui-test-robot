// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName
import com.intellij.remoterobot.data.ComponentData

class ComponentDataResponse(
    @SerializedName("componentData") val componentData: ComponentData?,
    @SerializedName("exception") val exception: Throwable?
)