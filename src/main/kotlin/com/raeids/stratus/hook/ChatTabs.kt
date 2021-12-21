package com.raeids.stratus.hook

import com.google.gson.*
import com.raeids.stratus.Stratus
import net.minecraft.client.Minecraft
import java.io.File

object ChatTabs {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val PARSER = JsonParser()
    val tabs = arrayListOf<ChatTab>()
    var isDoing = false
    var currentTab: ChatTab? = null
        set(value) {
            if (value != null) {
                field = value
                if (Minecraft.getMinecraft().theWorld != null) {
                    Minecraft.getMinecraft().ingameGUI.chatGUI.refreshChat()
                }
                println("current tab: ${value.name}")
            }
        }

    private val tabFile = File(Stratus.modDir, "chattabs.json")

    fun initialize() {
        if (!tabFile.exists()) {
            generateNewFile()
        } else {
            try {
                val chatTabJson = GSON.fromJson(tabFile.readText(), ChatTabsJson::class.java)
                chatTabJson.tabs.forEach {
                    tabs.add(GSON.fromJson(it.toString(), ChatTab::class.java))
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                tabFile.delete()
                generateNewFile()
            }
        }
        tabs.forEach {
            it.initialize()
        }
        currentTab = tabs[0]
    }

    fun shouldRender(message: String): Boolean {
        return currentTab?.shouldRender(message) ?: true
    }

    private fun generateNewFile() {
        tabFile.createNewFile()
        val jsonObject = JsonObject()
        val defaultTabs = generateDefaultTabs()
        jsonObject.add("tabs", defaultTabs)
        jsonObject.addProperty("version", 1)
        tabFile.writeText(jsonObject.toString())
    }

    private fun generateDefaultTabs(): JsonArray {
        val all = ChatTab("ALL", null, null, null, null, null, "")
        val party = ChatTab(
            "PARTY",
            listOf("§r§9Party §8> ", "§r§9P §8> ", "§eThe party was transferred to §r", "§eKicked §r"),
            null,
            listOf(
                "§r§ehas invited you to join their party!",
                "§r§eto the party! They have §r§c60 §r§eseconds to accept.§r",
                "§r§ehas disbanded the party!§r",
                "§r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before they are removed from the party.§r",
                " §r§ejoined the party.§r",
                " §r§ehas left the party.§r",
                " §r§ehas been removed from the party.§r",
                "§r§e because they were offline.§r"
            ),
            listOf("§cThe party was disbanded because all invites expired and the party was empty§r"),
            null,
            "/pc "
        )
        val guild = ChatTab(
            "GUILD",
            listOf("§r§2Guild > ", "§r§2G > "),
            null,
            null,
            null,
            null,
            "/gc "
        )
        val pm = ChatTab(
            "PM",
            listOf("§dTo ", "§dFrom "),
            null,
            null,
            null,
            null,
            "/r "
        )
        tabs.add(all)
        tabs.add(party)
        tabs.add(guild)
        tabs.add(pm)
        val jsonArray = JsonArray()
        jsonArray.add(PARSER.parse(GSON.toJson(all)).asJsonObject)
        jsonArray.add(PARSER.parse(GSON.toJson(party)).asJsonObject)
        jsonArray.add(PARSER.parse(GSON.toJson(guild)).asJsonObject)
        jsonArray.add(PARSER.parse(GSON.toJson(pm)).asJsonObject)
        return jsonArray
    }
}
