package org.polyfrost.chatting.chat

//? if > 1.8.9 {
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class ChatTabsJson(@SerializedName("tabs") val tabs: JsonArray, var version: Int) {

    override fun toString(): String {
        return "{\"tabs\": $tabs, \"version\": $version}"
    }

    companion object {
        const val VERSION = 6
    }
}
//?} else {
/*import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class ChatTabsJson(@SerializedName("tabs") val tabs: JsonArray, var version: Int) {

    override fun toString(): String {
        return "{\"tabs\": $tabs, \"version\": $version}"
    }

    companion object {
        const val VERSION = 6
    }
}
*///?}
