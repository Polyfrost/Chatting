package cc.woverflow.chatting.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils
import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.chat.ChatShortcuts
import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.gui.components.TabButton
import cc.woverflow.chatting.hook.ChatLineHook
import java.io.File

object ChattingConfig : Config(
    Mod(
        Chatting.NAME,
        ModType.UTIL_QOL,
        VigilanceMigrator(File(Chatting.oldModDir, Chatting.ID + ".toml").toPath().toString())
    ), "chatting.json"
) {

    @Dropdown(
        name = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var textRenderType = 1

    @Color(
        name = "Chat Background Color", category = "General", allowAlpha = false
    )
    var chatBackgroundColor = OneColor(0, 0, 0, 128)

    @Color(
        name = "Copy Chat Message Background Color", category = "General", allowAlpha = false
    )
    var hoveredChatBackgroundColor = OneColor(80, 80, 80, 128)

    @Switch(
        name = "Right Click to Copy Chat Message", category = "General"
    )
    var rightClickCopy = false

    @Switch(
        name = "Compact Input Box", category = "General"
    )
    var compactInputBox = false

    @Switch(
        name = "Inform Outdated Mods", category = "General"
    )
    var informForAlternatives = true

    @Switch(
        name = "Smooth Chat Messages",
        category = "Animations", subcategory = "Messages"
    )
    var smoothChat = true

    @Switch(
        name = "Smooth Chat Scrolling",
        category = "Animations", subcategory = "Scrolling"
    )
    var smoothScrolling = true

    @Switch(
        name = "Remove Scroll Bar",
        category = "Animations", subcategory = "Scrolling"
    )
    var removeScrollBar = true

    @Switch(
        name = "Show Chat Heads", description = "Show the chat heads of players in chat", category = "Chat Heads"
    )
    var showChatHeads = true

    @Switch(
        name = "Offset Non-Player Messages",
        description = "Offset all messages, even if a player has not been detected.",
        category = "Chat Heads"
    )
    var offsetNonPlayerMessages = false

    @Switch(
        name = "Hide Chat Head on Consecutive Messages",
        description = "Hide the chat head if the previous message was from the same player.",
        category = "Chat Heads"
    )
    var hideChatHeadOnConsecutiveMessages = true

    /*/
    @Property(
        type = PropertyType.SWITCH,
        name = "Show Timestamp",
        description = "Show message timestamp.",
        category = "General"
    )
    var showTimestamp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Timestamp Only On Hover",
        description = "Show timestamp only on mouse hover.",
        category = "General"
    )
    var showTimestampHover = true

     */

    @Info(
        text = "If Chatting detects a public chat message that seems like spam, and the probability is higher than this, it will hide it.\n" + "Made for Hypixel Skyblock. Set to 100% to disable. 95% is a reasonable threshold to use it at.\n" + "Note that this is not and never will be 100% accurate; however, it's pretty much guaranteed to block most spam.",
        size = 2,
        category = "Player Chats",
        type = InfoType.INFO
    )
    var ignored = false

    @Slider(
        min = 80F, max = 100F, name = "Spam Blocker Threshold", category = "Player Chats"
    )
    var spamThreshold = 100

    @Switch(
        name = "Custom SkyBlock Chat Formatting (remove ranks)", category = "Player Chats"
    )
    var customChatFormatting = false

    @Switch(
        name = "Completely Hide Spam", category = "Player Chats"
    )
    var hideSpam = false

    @Switch(
        name = "Custom Chat Height", category = "Chat Window"
    )
    var customChatHeight = false

    @Slider(
        min = 180F, max = 2160F, name = "Focused Height (px)", category = "Chat Window"
    )
    var focusedHeight = 180

    @Slider(
        min = 180F, max = 2160F, name = "Unfocused Height (px)", category = "Chat Window"
    )
    var unfocusedHeight = 180

    @Dropdown(
        name = "Screenshot Mode", category = "Screenshotting", options = ["Save To System", "Add To Clipboard", "Both"]
    )
    var copyMode = 0

    @Checkbox(
        name = "Chat Searching", category = "Searching"
    )
    var chatSearch = true

    @Switch(
        name = "Chat Tabs", category = "Tabs"
    )
    var chatTabs = true
        get() {
            if (!field) return false
            return if (hypixelOnlyChatTabs) {
                HypixelUtils.INSTANCE.isHypixel
            } else {
                true
            }
        }

    @Checkbox(
        name = "Enable Tabs Only on Hypixel", category = "Tabs"
    )
    var hypixelOnlyChatTabs = true

    @Switch(
        name = "Chat Shortcuts", category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) {
                HypixelUtils.INSTANCE.isHypixel
            } else {
                true
            }
        }

    @Checkbox(
        name = "Enable Shortcuts Only on Hypixel", category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Switch(
        name = "Remove Tooltip Background", category = "Tooltips"
    )
    var removeTooltipBackground = false

    @Dropdown(
        name = "Tooltip Text Render Type", category = "Tooltips", options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var tooltipTextRenderType = 1

    init {
        initialize()
        addDependency("offsetNonPlayerMessages", "showChatHeads")
        addDependency("hideChatHeadOnConsecutiveMessages", "showChatHeads")
        addListener("hideChatHeadOnConsecutiveMessages") {
            ChatLineHook.chatLines.map { it.get() as ChatLineHook? }.forEach { it?.updatePlayerInfo() }
        }
        addListener("chatTabs") {
            ChatTabs.initialize()
            if (!chatTabs) {
                val dummy = ChatTab(
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
                dummy.initialize()
                ChatTabs.currentTabs.clear()
                ChatTabs.currentTabs.add(dummy)
            } else {
                ChatTabs.currentTabs.clear()
                ChatTabs.currentTabs.add(ChatTabs.tabs[0])
            }
        }
        addListener("chatShortcuts") {
            ChatShortcuts.initialize()
        }
        // addDependency("showTimestampHover", "showTimestamp")
    }
}
