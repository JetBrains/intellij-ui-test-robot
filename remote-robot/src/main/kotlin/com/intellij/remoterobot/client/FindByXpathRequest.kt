package com.intellij.remoterobot.client

import com.google.gson.annotations.SerializedName

class FindByXpathRequest(@SerializedName("xpath") val xpath: String)