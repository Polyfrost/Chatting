package cc.woverflow.chatting.chat

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class ChatTabsJson(@SerializedName("tabs") val tabs: JsonArray, var version: Int) {

    override fun toString(): String {
        return "{\"tabs\": $tabs, \"version\": $version}"
    }

    companion object {
        const val VERSION = 4
    }
}