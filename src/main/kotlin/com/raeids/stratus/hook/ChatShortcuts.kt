package com.raeids.stratus.hook

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.raeids.stratus.Stratus
import java.io.File

object ChatShortcuts {
    private val shortcutsFile = File(Stratus.modDir, "chatshortcuts.json")
    private val PARSER = JsonParser()

    val shortcuts = mutableSetOf<Pair<String, String>>()


    fun initialize() {
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
            if (command == it.first || (command.startsWith(it.first) && command.substringAfter(it.first).startsWith(" "))) {
                return command.replaceFirst(it.first, it.second)
            }
        }
        return command
    }
}