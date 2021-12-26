package com.raeids.stratus.chat

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class ChatTabsJson(@SerializedName("tabs") val tabs: JsonArray, @SerializedName("version") var version: Int) {

    override fun toString(): String {
        return "{\"tabs\": $tabs, \"version\": \"$version\"}"
    }
}