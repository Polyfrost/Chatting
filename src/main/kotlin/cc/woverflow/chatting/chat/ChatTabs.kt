package cc.woverflow.chatting.chat

import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.gui.components.TabButton
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import net.minecraft.client.Minecraft
import net.minecraft.util.IChatComponent
import java.io.File

object ChatTabs {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val PARSER = JsonParser()
    val tabs = arrayListOf<ChatTab>()
    var currentTabs: ArrayList<ChatTab?> = object : ArrayList<ChatTab?>() {
        override fun add(element: ChatTab?): Boolean {
            if (element == null) return false
            val returnValue = super.add(element)
            if (Minecraft.getMinecraft().theWorld != null && returnValue) {
                Minecraft.getMinecraft().ingameGUI.chatGUI.refreshChat()
            }
            return returnValue
        }
    }
    var hasCancelledAnimation = false
    private var initialized = false

    private val tabFile = ConfigUtils.getProfileFile("chattabs.json")
    private val oldTabFile = File(Chatting.oldModDir, "chattabs.json")

    fun initialize() {
        if (initialized) {
            return
        } else {
            initialized = true
        }
        if (!tabFile.exists()) {
            if (oldTabFile.exists()) {
                tabFile.writeText(oldTabFile.readText())
                handleFile()
            } else {
                generateNewFile()
            }
        } else {
            handleFile()
        }
        tabs.forEach {
            it.initialize()
        }
        currentTabs.clear()
        currentTabs.add(tabs[0])
    }

    private fun handleFile() {
        try {
            val chatTabJson = GSON.fromJson(tabFile.readText(), ChatTabsJson::class.java)
            when (chatTabJson.version) {
                1 -> {
                    // ver 2 adds `enabled`
                    chatTabJson.tabs.forEach {
                        applyVersion2Changes(it.asJsonObject)
                        applyVersion3Changes(it.asJsonObject)
                        applyVersion4Changes(it.asJsonObject)
                        applyVersion5Changes(it.asJsonObject)
                        applyVersion6Changes(it.asJsonObject)
                    }
                    chatTabJson.version = ChatTabsJson.VERSION
                    tabFile.writeText(GSON.toJson(chatTabJson))
                }
                2 -> {
                    // ver 3 adds ignore_
                    chatTabJson.tabs.forEach {
                        applyVersion3Changes(it.asJsonObject)
                        applyVersion4Changes(it.asJsonObject)
                        applyVersion5Changes(it.asJsonObject)
                        applyVersion6Changes(it.asJsonObject)
                    }
                    chatTabJson.version = ChatTabsJson.VERSION
                    tabFile.writeText(GSON.toJson(chatTabJson))
                }
                3 -> {
                    // ver 4 adds color options
                    chatTabJson.tabs.forEach {
                        applyVersion4Changes(it.asJsonObject)
                        applyVersion5Changes(it.asJsonObject)
                        applyVersion6Changes(it.asJsonObject)
                    }
                    chatTabJson.version = ChatTabsJson.VERSION
                    tabFile.writeText(GSON.toJson(chatTabJson))
                }
                4 -> {
                    // ver 5 adds lowercase
                    chatTabJson.tabs.forEach {
                        applyVersion5Changes(it.asJsonObject)
                        applyVersion6Changes(it.asJsonObject)
                    }
                    chatTabJson.version = ChatTabsJson.VERSION
                    tabFile.writeText(GSON.toJson(chatTabJson))
                }
                5 -> {
                    // ver 6 changes pm regex
                    chatTabJson.tabs.forEach {
                        applyVersion6Changes(it.asJsonObject)
                    }
                    chatTabJson.version = ChatTabsJson.VERSION
                    tabFile.writeText(GSON.toJson(chatTabJson))
                }
            }
            chatTabJson.tabs.forEach {
                val chatTab = GSON.fromJson(it.toString(), ChatTab::class.java)
                if (chatTab.enabled) {
                    tabs.add(chatTab)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            tabFile.delete()
            generateNewFile()
        }
    }

    private fun applyVersion2Changes(json: JsonObject) {
        json.addProperty("enabled", true)
    }

    private fun applyVersion3Changes(json: JsonObject) {
        json.add("ignore_starts", JsonArray())
        json.add("ignore_contains", JsonArray())
        json.add("ignore_ends", JsonArray())
        json.add("ignore_equals", JsonArray())
        json.add("ignore_regex", JsonArray())
    }

    private fun applyVersion4Changes(json: JsonObject) {
        json.addProperty("color", TabButton.color)
        json.addProperty("hovered_color", TabButton.hoveredColor)
        json.addProperty("selected_color", TabButton.selectedColor)
    }

    private fun applyVersion5Changes(json: JsonObject) {
        json.addProperty("lowercase", false)
    }

    private fun applyVersion6Changes(json: JsonObject) {
        if (json.has("starts")) {
            val starts = json["starts"].asJsonArray
            var detected = false
            starts.iterator().let {
                while (it.hasNext()) {
                    when (it.next().asString) {
                        "To " -> {
                            detected = true
                            it.remove()
                        }
                        "From " -> {
                            detected = true
                            it.remove()
                        }
                    }
                }
            }
            if (detected) {
                json.add("regex", JsonArray().apply {
                    add(JsonPrimitive("^(?<type>§dTo|§dFrom) (?<prefix>.+): §r(?<message>.*)(?:§r)?\$"))
                })
                json.remove("unformatted")
                json.addProperty("unformatted", false)
            }
        }
        if (json.has("ends")) {
            val ends = json["ends"].asJsonArray
            var detected = false
            ends.iterator().let {
                while (it.hasNext()) {
                    when (it.next().asString) {
                        "§r§ehas invited you to join their party!", -> {
                            detected = true
                            it.remove()
                        }
                    }
                }
            }
            if (detected) {
                json.add("contains", JsonArray().apply {
                    add(JsonPrimitive("§r§ehas invited you to join their party!"))
                })
            }
        }
    }

    fun shouldRender(message: IChatComponent): Boolean {
        if (currentTabs.isEmpty()) return true
        for (tab in currentTabs) {
            if (tab?.shouldRender(message) == true) {
                return true
            }
        }
        return false
    }

    private fun generateNewFile() {
        tabFile.createNewFile()
        val jsonObject = JsonObject()
        val defaultTabs = generateDefaultTabs()
        jsonObject.add("tabs", defaultTabs)
        jsonObject.addProperty("version", ChatTabsJson.VERSION)
        tabFile.writeText(GSON.toJson(jsonObject))
    }

    private fun generateDefaultTabs(): JsonArray {
        val all = ChatTab(
            true,
            "ALL",
            unformatted = false,
            lowercase = false,
            startsWith = null,
            contains = null,
            endsWith = null,
            equals = null,
            uncompiledRegex = null,
            ignoreStartsWith = null,
            ignoreContains = null,
            ignoreEndsWith = null,
            ignoreEquals = null,
            uncompiledIgnoreRegex = null,
            color = TabButton.color,
            hoveredColor = TabButton.hoveredColor,
            selectedColor = TabButton.selectedColor,
            prefix = ""
        )
        val party = ChatTab(
            true,
            "PARTY",
            unformatted = false,
            lowercase = false,
            startsWith = listOf("§r§9Party §8> ", "§r§9P §8> ", "§eThe party was transferred to §r", "§eKicked §r"),
            contains = listOf("§r§ehas invited you to join their party!"),
            endsWith = listOf(
                "§r§eto the party! They have §r§c60 §r§eseconds to accept.§r",
                "§r§ehas disbanded the party!§r",
                "§r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before they are removed from the party.§r",
                " §r§ejoined the party.§r",
                " §r§ehas left the party.§r",
                " §r§ehas been removed from the party.§r",
                "§r§e because they were offline.§r"
            ),
            equals = listOf("§cThe party was disbanded because all invites expired and the party was empty§r"),
            uncompiledRegex = listOf( //regexes from https://github.com/kwevin/Hychat-Tabs/blob/main/tabs/re-add%20prefixes%20%26%20fix%20shortened%20tags/chat.json cause i cant write regex
                "(§r)*(§9Party §8\u003e)+(.*)",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§einvited §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto the party! They have §r§c60 §r§eseconds to accept\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas left the party\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ejoined the party\\.§r",
                "§eYou left the party\\.§r",
                "§eYou have joined §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)\u0027s §r§eparty!§r",
                "§cThe party was disbanded because all invites expired and the party was empty§r",
                "§cYou cannot invite that player since they\u0027re not online\\.§r",
                "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e, warped you to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e\u0027s house\\.§r",
                "§eSkyBlock Party Warp §r§7\\([0-9]+ players?\\)§r",
                "§a. §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§f §r§awarped to your server§r",
                "§eYou summoned §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§f §r§eto your server\\.§r",
                "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e, warped you to their house\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§aenabled Private Game§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§cdisabled Private Game§r",
                "§cThe party is now muted\\. §r",
                "§aThe party is no longer muted\\.§r",
                "§cThere are no offline players to remove\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas been removed from the party\\.§r",
                "§eThe party was transferred to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eby §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has promoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Leader§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has promoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Moderator§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eis now a Party Moderator§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has demoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Member§r",
                "§cYou can\u0027t demote yourself!§r",
                "§6Party Members \\([0-9]+\\)§r",
                "§eParty Leader: §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) ?§r(?:§[a-zA-Z0-9]).§r",
                "§eParty Members: §r(?:(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r(?:§[a-zA-Z0-9]) . §r)+",
                "§eParty Moderators: §r(?:(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r(?:§[a-zA-Z0-9]) . §r)+",
                "§eThe party invite to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas expired§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§cdisabled All Invite§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§aenabled All Invite§r",
                "§cYou cannot invite that player\\.§r",
                "§cYou are not allowed to invite players\\.§r",
                "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before the party is disbanded\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before they are removed from the party.§r",
                "§cYou are not in a party right now\\.§r",
                "§cThis party is currently muted\\.§r",
                "(§r)*(§9P §8\u003e)+(.*)"
            ),
            ignoreStartsWith = null,
            ignoreContains = null,
            ignoreEndsWith = null,
            ignoreEquals = null,
            uncompiledIgnoreRegex = null,
            color = TabButton.color,
            hoveredColor = TabButton.hoveredColor,
            selectedColor = TabButton.selectedColor,
            prefix = "/pc "
        )
        val guild = ChatTab(
            true,
            "GUILD",
            unformatted = true,
            lowercase = false,
            startsWith = listOf("Guild >", "G >"),
            contains = null,
            endsWith = null,
            equals = null,
            uncompiledRegex = null,
            ignoreStartsWith = null,
            ignoreContains = null,
            ignoreEndsWith = null,
            ignoreEquals = null,
            uncompiledIgnoreRegex = null,
            color = TabButton.color,
            hoveredColor = TabButton.hoveredColor,
            selectedColor = TabButton.selectedColor,
            prefix = "/gc "
        )
        val pm = ChatTab(
            true,
            "PM",
            unformatted = false,
            lowercase = false,
            startsWith = null,
            contains = null,
            endsWith = null,
            equals = null,
            uncompiledRegex = listOf("^(?<type>§dTo|§dFrom) (?<prefix>.+): §r(?<message>.*)(?:§r)?\$"),
            ignoreStartsWith = null,
            ignoreContains = null,
            ignoreEndsWith = null,
            ignoreEquals = null,
            uncompiledIgnoreRegex = null,
            color = TabButton.color,
            hoveredColor = TabButton.hoveredColor,
            selectedColor = TabButton.selectedColor,
            prefix = "/r "
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
