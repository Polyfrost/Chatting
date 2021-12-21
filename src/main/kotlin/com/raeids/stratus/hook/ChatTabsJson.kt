package com.raeids.stratus.hook

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class ChatTabsJson(@SerializedName("tabs") val tabs: JsonArray, @SerializedName("version") val version: Int)