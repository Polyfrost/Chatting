package cc.woverflow.chatting.chat

import cc.woverflow.chatting.Chatting
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

object ChatShortcuts {
    private val shortcutsFile = File(Chatting.modDir, "chatshortcuts.json")
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
            shortcutsFile.createNewFile()
            shortcutsFile.writeText(
                JsonObject().toString()
            )
        } else {
            val jsonObj = PARSER.parse(shortcutsFile.readText()).asJsonObject
            for (shortcut in jsonObj.entrySet()) {
                shortcuts.add(shortcut.key to shortcut.value.asString)
            }
        }
    }

    fun removeShortcut(key: String) {
        shortcuts.removeIf { it.first == key }
        val jsonObj = PARSER.parse(shortcutsFile.readText()).asJsonObject
        jsonObj.remove(key)
        shortcutsFile.writeText(jsonObj.toString())
    }

    fun writeShortcut(key: String, value: String) {
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