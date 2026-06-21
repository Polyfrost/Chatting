package org.polyfrost.chatting.chat

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Expands user-defined chat shortcuts (e.g. typing `gg` sends `good game`) when a message is sent.
 *
 * Ported from the 1.8.9 version; the store now lives in the Fabric config directory and the lookup
 * is applied from [org.polyfrost.chatting.mixin.ChatScreenMixin].
 */
object ChatShortcuts {

    private val shortcutsFile = FabricLoader.getInstance().configDir.resolve("chatting").resolve("chatshortcuts.json")

    private var initialized = false

    /** Sorted longest-first so a longer shortcut wins over a prefix of it. */
    val shortcuts = object : ArrayList<Pair<String, String>>() {
        private val comparator = Comparator<Pair<String, String>> { o1, o2 -> o2.first.length.compareTo(o1.first.length) }

        override fun add(element: Pair<String, String>): Boolean {
            val value = super.add(element)
            sortWith(comparator)
            return value
        }
    }

    fun initialize() {
        if (initialized) return
        initialized = true
        shortcutsFile.parent.createDirectories()
        if (shortcutsFile.exists()) {
            try {
                val obj = JsonParser.parseString(shortcutsFile.readText()).asJsonObject
                shortcuts.clear()
                for (entry in obj.entrySet()) shortcuts.add(entry.key to entry.value.asString)
                return
            } catch (_: Throwable) {
                // fall through and reset on corruption
            }
        }
        shortcutsFile.writeText(JsonObject().toString())
    }

    fun writeShortcut(key: String, value: String) {
        shortcuts.add(key to value)
        val obj = runCatching { JsonParser.parseString(shortcutsFile.readText()).asJsonObject }.getOrElse { JsonObject() }
        obj.addProperty(key, value)
        shortcutsFile.writeText(obj.toString())
    }

    fun removeShortcut(key: String) {
        shortcuts.removeIf { it.first == key }
        val obj = runCatching { JsonParser.parseString(shortcutsFile.readText()).asJsonObject }.getOrElse { JsonObject() }
        obj.remove(key)
        shortcutsFile.writeText(obj.toString())
    }

    /** Applies the first matching shortcut to a sent (non-command) message. */
    fun handleSentMessage(message: String): String {
        if (!ChattingConfig.chatShortcuts) return message
        shortcuts.forEach {
            if (message == it.first || (message.startsWith(it.first) && message.substringAfter(it.first).startsWith(" "))) {
                return message.replaceFirst(it.first, it.second)
            }
        }
        return message
    }
}
