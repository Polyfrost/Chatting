package org.polyfrost.chatting.chat

//? if > 1.8.9 {
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import org.polyfrost.chatting.config.ChattingConfig
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ChatShortcuts {

    private val shortcutsFile = FabricLoader.getInstance().configDir.resolve("chatting").resolve("chatshortcuts.json")

    private var initialized = false

    /** sorted longest first so a longer shortcut wins over a prefix of it */
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
        shortcuts.removeIf { it.first == key }
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

    /** the leading slash is preserved so vanilla still routes the expansion through the command packet */
    fun handleSentCommand(message: String): String {
        if (!ChattingConfig.chatShortcuts || !message.startsWith("/")) return message
        val command = message.substring(1)
        shortcuts.forEach {
            if (command == it.first || (command.startsWith(it.first) && command.substringAfter(it.first).startsWith(" "))) {
                return "/" + command.replaceFirst(it.first, it.second)
            }
        }
        return message
    }
}
//?} else {
/*import org.polyfrost.chatting.Chatting
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.polyfrost.oneconfig.api.config.v1.ConfigManager
import kotlin.io.path.*

object ChatShortcuts {
    private val oldShortcutsFile = Chatting.oldModDir.resolve("chatshortcuts.json")
    private val shortcutsFile = ConfigManager.active().folder.resolve("chatshortcuts.json")
    private val PARSER = JsonParser()

    private var initialized = false

    val shortcuts = object : ArrayList<Pair<String, String>>() {
        private val comparator = Comparator<Pair<String, String>> { o1, o2 ->
            return@Comparator o2.first.length.compareTo(o1.first.length)
        }

        override fun add(element: Pair<String, String>): Boolean {
            val value = super.add(element)
            sortWith(comparator)
            return value
        }
    }

    fun initialize() {
        if (initialized) {
            return
        } else {
            initialized = true
        }
        if (!shortcutsFile.exists()) {
            if (oldShortcutsFile.exists()) {
                // Migrate before parsing so the aliases are usable in the same
                // client session, not only after the next restart.
                oldShortcutsFile.moveTo(shortcutsFile)
            } else {
                shortcutsFile.createFile()
                shortcutsFile.writeText(JsonObject().toString())
            }
        }
        try {
            val jsonObj = PARSER.parse(shortcutsFile.readText()).asJsonObject
            for (shortcut in jsonObj.entrySet()) {
                shortcuts.add(shortcut.key to shortcut.value.asString)
            }
        } catch (_: Throwable) {
            shortcutsFile.moveTo(shortcutsFile.parent.resolve("chatshortcuts.json.bak"))
            shortcutsFile.writeText(JsonObject().toString())
        }
    }

    fun removeShortcut(key: String) {
        shortcuts.removeIf { it.first == key }
        val jsonObj = PARSER.parse(shortcutsFile.readText()).asJsonObject
        jsonObj.remove(key)
        shortcutsFile.writeText(jsonObj.toString())
    }

    fun writeShortcut(key: String, value: String) {
        // Updating an alias must replace its in-memory entry too; otherwise a
        // stale value can win when the list is searched by command length.
        shortcuts.removeIf { it.first == key }
        shortcuts.add(key to value)
        val jsonObj = PARSER.parse(shortcutsFile.readText()).asJsonObject
        jsonObj.addProperty(key, value)
        shortcutsFile.writeText(jsonObj.toString())
    }

    fun handleSentCommand(command: String): String {
        shortcuts.forEach {
            if (command == it.first || (command.startsWith(it.first) && command.substringAfter(it.first)
                    .startsWith(" "))
            ) {
                return command.replaceFirst(it.first, it.second)
            }
        }
        return command
    }
}
*///?}
