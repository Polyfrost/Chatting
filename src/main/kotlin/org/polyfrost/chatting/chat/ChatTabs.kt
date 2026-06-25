package org.polyfrost.chatting.chat

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.polyfrost.chatting.config.ChattingConfig
import org.polyfrost.chatting.hook.ChatComponentHook
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ChatTabs {
    private val GSON = GsonBuilder().setPrettyPrinting().create()

    val tabs = arrayListOf<ChatTab>()
    val currentTabs = arrayListOf<ChatTab>()

    private var initialized = false

    private val tabFile = FabricLoader.getInstance().configDir.resolve("chatting").resolve("chattabs.json")

    fun initialize() {
        if (initialized) return
        initialized = true
        tabFile.parent.createDirectories()
        if (!tabFile.exists()) {
            generateNewFile()
        } else {
            handleFile()
        }
        tabs.forEach { it.initialize() }
        currentTabs.clear()
        if (tabs.isNotEmpty()) currentTabs.add(tabs[0])
    }

    fun shouldFilter(): Boolean = ChattingConfig.chatTabs && tabs.isNotEmpty()

    fun shouldRender(message: Component): Boolean {
        if (currentTabs.isEmpty()) return true
        for (tab in currentTabs) if (tab.shouldRender(message)) return true
        return false
    }

    fun click(tab: ChatTab, shift: Boolean) {
        if (shift) {
            if (currentTabs.contains(tab)) currentTabs.remove(tab) else currentTabs.add(tab)
        } else {
            currentTabs.clear()
            currentTabs.add(tab)
        }
        refresh()
    }

    fun isSelected(tab: ChatTab): Boolean = currentTabs.contains(tab)

    fun refresh() {
        //? if >=26.2 {
        /*val chat = Minecraft.getInstance().gui?.hud?.chat ?: return
        *///?} else {
        val chat = Minecraft.getInstance().gui?.chat ?: return
        //?}
        (chat as? ChatComponentHook)?.`chatting$refresh`()
    }

    fun applyPrefix(message: String): String {
        if (!ChattingConfig.chatTabs || message.startsWith("/")) return message
        val tab = currentTabs.firstOrNull { !it.prefix.isNullOrEmpty() } ?: return message
        return tab.prefix + message
    }

    private fun handleFile() {
        try {
            val chatTabJson = GSON.fromJson(tabFile.readText(), ChatTabsJson::class.java)
            if (chatTabJson.version < ChatTabsJson.VERSION) {
                chatTabJson.tabs.forEach {
                    val obj = it.asJsonObject
                    if (chatTabJson.version < 2) applyVersion2Changes(obj)
                    if (chatTabJson.version < 3) applyVersion3Changes(obj)
                    if (chatTabJson.version < 4) applyVersion4Changes(obj)
                    if (chatTabJson.version < 5) applyVersion5Changes(obj)
                    if (chatTabJson.version < 6) applyVersion6Changes(obj)
                }
                chatTabJson.version = ChatTabsJson.VERSION
                tabFile.writeText(GSON.toJson(chatTabJson))
            }
            chatTabJson.tabs.forEach {
                val chatTab = GSON.fromJson(it.toString(), ChatTab::class.java)
                if (chatTab.enabled) tabs.add(chatTab)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            runCatching { tabFile.writeText("") }
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
        json.addProperty("color", ChatTab.COLOR)
        json.addProperty("hovered_color", ChatTab.HOVERED_COLOR)
        json.addProperty("selected_color", ChatTab.SELECTED_COLOR)
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
                        "To ", "From " -> {
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
                        "§r§ehas invited you to join their party!" -> {
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

    private fun generateNewFile() {
        tabs.clear()
        val jsonObject = JsonObject()
        jsonObject.add("tabs", generateDefaultTabs())
        jsonObject.addProperty("version", ChatTabsJson.VERSION)
        tabFile.writeText(GSON.toJson(jsonObject))
    }

    private fun generateDefaultTabs(): JsonArray {
        val all = ChatTab(
            true, "ALL", unformatted = false, lowercase = false,
            startsWith = null, contains = null, endsWith = null, equals = null, uncompiledRegex = null,
            ignoreStartsWith = null, ignoreContains = null, ignoreEndsWith = null, ignoreEquals = null, uncompiledIgnoreRegex = null,
            color = ChatTab.COLOR, hoveredColor = ChatTab.HOVERED_COLOR, selectedColor = ChatTab.SELECTED_COLOR, prefix = ""
        )
        val party = ChatTab(
            true, "PARTY", unformatted = false, lowercase = false,
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
            uncompiledRegex = listOf(
                "(§r)*(§9Party §8>)+(.*)",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§einvited §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto the party! They have §r§c60 §r§eseconds to accept\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas left the party\\.§r",
                "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ejoined the party\\.§r",
                "§eYou left the party\\.§r",
                "§eYou have joined §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)'s §r§eparty!§r",
                "§cThe party was disbanded because all invites expired and the party was empty§r",
                "§cYou cannot invite that player since they're not online\\.§r",
                "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e, warped you to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e's house\\.§r",
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
                "§cYou can't demote yourself!§r",
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
                "(§r)*(§9P §8>)+(.*)"
            ),
            ignoreStartsWith = null, ignoreContains = null, ignoreEndsWith = null, ignoreEquals = null, uncompiledIgnoreRegex = null,
            color = ChatTab.COLOR, hoveredColor = ChatTab.HOVERED_COLOR, selectedColor = ChatTab.SELECTED_COLOR, prefix = "/pc "
        )
        val guild = ChatTab(
            true, "GUILD", unformatted = true, lowercase = false,
            startsWith = listOf("Guild >", "G >"),
            contains = null, endsWith = null, equals = null, uncompiledRegex = null,
            ignoreStartsWith = null, ignoreContains = null, ignoreEndsWith = null, ignoreEquals = null, uncompiledIgnoreRegex = null,
            color = ChatTab.COLOR, hoveredColor = ChatTab.HOVERED_COLOR, selectedColor = ChatTab.SELECTED_COLOR, prefix = "/gc "
        )
        val pm = ChatTab(
            true, "PM", unformatted = false, lowercase = false,
            startsWith = null, contains = null, endsWith = null, equals = null,
            uncompiledRegex = listOf("^(?<type>§dTo|§dFrom) (?<prefix>.+): §r(?<message>.*)(?:§r)?\$"),
            ignoreStartsWith = null, ignoreContains = null, ignoreEndsWith = null, ignoreEquals = null, uncompiledIgnoreRegex = null,
            color = ChatTab.COLOR, hoveredColor = ChatTab.HOVERED_COLOR, selectedColor = ChatTab.SELECTED_COLOR, prefix = "/r "
        )
        tabs.add(all)
        tabs.add(party)
        tabs.add(guild)
        tabs.add(pm)
        val jsonArray = JsonArray()
        listOf(all, party, guild, pm).forEach { jsonArray.add(JsonParser.parseString(GSON.toJson(it)).asJsonObject) }
        return jsonArray
    }
}
